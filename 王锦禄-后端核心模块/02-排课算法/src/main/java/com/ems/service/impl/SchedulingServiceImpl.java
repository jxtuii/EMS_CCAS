package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.dto.PublishResultDTO;
import com.ems.dto.SchedulingResultDTO;
import com.ems.entity.*;
import com.ems.mapper.*;
import com.ems.service.SchedulingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SchedulingServiceImpl implements SchedulingService {

    private static final Logger log = LoggerFactory.getLogger(SchedulingServiceImpl.class);

    @Autowired
    private TeachingPlanMapper planMapper;
    @Autowired
    private TeachingTaskMapper taskMapper;
    @Autowired
    private TimeSlotMapper timeSlotMapper;
    @Autowired
    private ScheduleMapper scheduleMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private TeachingApplicationMapper applicationMapper;

    @Override
    @Transactional
    public SchedulingResultDTO schedule(String inputSemester, String outputSemester) {
        SchedulingResultDTO result = new SchedulingResultDTO();
        List<String> conflicts = new ArrayList<>();
        int successCount = 0;

        List<Schedule> oldSchedules = scheduleMapper.selectBySemester(outputSemester);
        if (!oldSchedules.isEmpty()) {
            log.info("清理学期[{}]旧的排课数据: {}条", outputSemester, oldSchedules.size());
            for (Schedule s : oldSchedules) {
                scheduleMapper.deleteById(s.getId());
            }
        }

        List<TeachingPlan> allPlans = planMapper.selectAllWithNames();
        List<TeachingPlan> plans = allPlans.stream()
                .filter(p -> inputSemester.equals(p.getSemester()) && p.getStatus() != null && p.getStatus() == 4)
                .toList();

        log.info("开始执行排课: 输入学期={}, 输出学期={}, 待排课计划数={}", inputSemester, outputSemester, plans.size());
        if (plans.isEmpty()) {
            log.warn("没有待排课的教学计划");
            result.setSuccessCount(0);
            result.setConflictCount(0);
            result.setConflicts(List.of("没有待排课的教学计划（status=4），请先完成学院审核流程"));
            return result;
        }

        List<TeachingApplication> approvedApps = applicationMapper.selectList(
                new LambdaQueryWrapper<TeachingApplication>()
                        .eq(TeachingApplication::getStatus, 4));
        log.info("已审批通过的申报数: {}", approvedApps.size());

        List<TimeSlot> allTimeSlots = timeSlotMapper.selectList(null);
        log.info("可用时间片数: {}", allTimeSlots.size());

        Map<Long, List<TeachingPlan>> plansByClass = plans.stream()
                .collect(Collectors.groupingBy(TeachingPlan::getClassId));

        List<Schedule> existingSchedules = new ArrayList<>();
        java.util.Set<Long> scheduledPlanIds = new java.util.HashSet<>();

        for (Map.Entry<Long, List<TeachingPlan>> entry : plansByClass.entrySet()) {
            Long classId = entry.getKey();
            List<TeachingPlan> classPlans = entry.getValue();

            for (TeachingPlan plan : classPlans) {
                TeachingApplication app = approvedApps.stream()
                        .filter(a -> a.getTeachingPlanId().equals(plan.getId()))
                        .findFirst().orElse(null);

                if (app == null) {
                    log.warn("排课跳过: 课程[{}]尚未有学院审核通过的教师申报", plan.getCourseName());
                    conflicts.add("课程[" + plan.getCourseName() + "]班级[" + plan.getClassName()
                            + "]缺少学院审核通过的教师申报");
                    continue;
                }

                Long teacherId = app.getTeacherId();
                int weeklyHours = plan.getWeeklyHours() != null ? plan.getWeeklyHours() : 2;

                List<Long> assignedSlots = new ArrayList<>();
                boolean scheduled = findSlots(classId, teacherId, weeklyHours, existingSchedules,
                        allTimeSlots, assignedSlots, outputSemester);

                if (scheduled) {
                    scheduledPlanIds.add(plan.getId());
                    log.info("课程[{}]排课成功, 教师ID={}, 分配了{}个时段, 输出学期={}",
                            plan.getCourseName(), teacherId, assignedSlots.size(), outputSemester);
                    for (Long slotId : assignedSlots) {
                        Schedule schedule = new Schedule();
                        schedule.setSemester(outputSemester);
                        schedule.setClassId(classId);
                        schedule.setCourseId(plan.getCourseId());
                        schedule.setTeacherId(teacherId);
                        schedule.setTimeSlotId(slotId);
                        scheduleMapper.insert(schedule);
                        existingSchedules.add(schedule);
                    }
                    successCount++;
                } else {
                    log.warn("排课冲突: 课程[{}]班级[{}]找不到空闲时段",
                            plan.getCourseName(), plan.getClassName());
                    conflicts.add("课程[" + plan.getCourseName() + "]班级[" + plan.getClassName()
                            + "]找不到空闲时段");
                }
            }
        }

        // ★ 只更新排课成功的计划状态为 5

        if (!scheduledPlanIds.isEmpty()) {
            for (TeachingPlan plan : plans) {
                if (scheduledPlanIds.contains(plan.getId())) {
                    plan.setStatus(5);
                    planMapper.updateById(plan);
                }
            }
        }

        log.info("排课完成: 输入学期={}, 输出学期={}, 成功={}, 冲突={}", inputSemester, outputSemester, successCount, conflicts.size());

        result.setSuccessCount(successCount);
        result.setConflictCount(conflicts.size());
        result.setConflicts(conflicts);

        return result;
    }

    @Override
    @Transactional
    public PublishResultDTO publishTasks(String inputSemester, String outputSemester) {
        log.info("发布教学任务: 输入学期={}, 输出学期={}", inputSemester, outputSemester);

        List<TeachingPlan> plans = planMapper.selectAllWithNames().stream()
                .filter(p -> inputSemester.equals(p.getSemester()) && p.getStatus() != null && p.getStatus() == 5)
                .collect(Collectors.toList());

        List<TeachingApplication> approvedApps = applicationMapper.selectList(
                new LambdaQueryWrapper<TeachingApplication>()
                        .eq(TeachingApplication::getStatus, 4));

        List<String> details = new ArrayList<>();
        int createdCount = 0;

        for (TeachingPlan plan : plans) {
            TeachingApplication app = approvedApps.stream()
                    .filter(a -> a.getTeachingPlanId().equals(plan.getId()))
                    .findFirst().orElse(null);
            if (app == null) continue;

            List<TeachingTask> existing = taskMapper.selectList(
                    new LambdaQueryWrapper<TeachingTask>()
                            .eq(TeachingTask::getTeacherId, app.getTeacherId())
                            .eq(TeachingTask::getCourseId, plan.getCourseId())
                            .eq(TeachingTask::getClassId, plan.getClassId())
                            .eq(TeachingTask::getSemester, outputSemester));

            if (existing.isEmpty()) {
                TeachingTask task = new TeachingTask();
                task.setTeacherId(app.getTeacherId());
                task.setCourseId(plan.getCourseId());
                task.setClassId(plan.getClassId());
                task.setSemester(outputSemester);
                task.setRoleType("主讲");
                task.setTotalHours(plan.getTotalHours());
                task.setWeeklyHours(plan.getWeeklyHours());
                task.setStatus(2);
                taskMapper.insert(task);
                createdCount++;
                details.add("教师ID=" + app.getTeacherId() + " " + plan.getCourseName()
                        + " → " + plan.getClassName() + " [输出学期=" + outputSemester + "]");
            }
        }

        return new PublishResultDTO(createdCount, details);
    }

    /**
     * 时间片分配算法。
     *
     * 优先级（从高到低）：
     *   周3学时 → 晚间(9-11节) 连续3节  > 晚间同天3节  > 晚间+白天拼凑
     *   通用   → 同天连续2节+同日补齐 > 同天凑够 > 跨天拼凑
     */
    private boolean findSlots(Long classId, Long teacherId, int weeklyHours,
                              List<Schedule> existingSchedules, List<TimeSlot> allTimeSlots,
                              List<Long> assignedSlots, String semester) {
        // ── 计算已占用时间片 ──
        Set<Long> classOcc = existingSchedules.stream()
                .filter(s -> s.getClassId().equals(classId))
                .map(Schedule::getTimeSlotId).collect(Collectors.toSet());
        Set<Long> teacherOcc = existingSchedules.stream()
                .filter(s -> s.getTeacherId().equals(teacherId))
                .map(Schedule::getTimeSlotId).collect(Collectors.toSet());

        List<TimeSlot> avail = allTimeSlots.stream()
                .filter(ts -> !classOcc.contains(ts.getId()) && !teacherOcc.contains(ts.getId()))
                .collect(Collectors.toList());
        if (avail.isEmpty()) return false;

        int needed = weeklyHours;

        // ══════════════════ 周3学时：晚间优先 ══════════════════
        if (needed == 3) {
            List<TimeSlot> evening = avail.stream()
                    .filter(ts -> ts.getSectionNo() >= 9).collect(Collectors.toList());
            if (evening.size() >= 3) {
                // 按天分组，随机排序(公正)
                Map<Integer, List<TimeSlot>> eByDay = evening.stream()
                        .collect(Collectors.groupingBy(TimeSlot::getWeekday));
                List<Integer> edays = new ArrayList<>(eByDay.keySet());
                Collections.shuffle(edays, new Random());

                // (a) 同一天连续3节晚间
                for (int d : edays) {
                    List<TimeSlot> slots = eByDay.get(d);
                    slots.sort(Comparator.comparingInt(TimeSlot::getSectionNo));
                    for (int i = 0; i + 2 < slots.size(); i++) {
                        if (slots.get(i+1).getSectionNo() == slots.get(i).getSectionNo()+1
                         && slots.get(i+2).getSectionNo() == slots.get(i+1).getSectionNo()+1) {
                            for (int k = 0; k < 3; k++) assignedSlots.add(slots.get(i+k).getId());
                            return true;
                        }
                    }
                }
                // (b) 同一天任意3节晚间
                for (int d : edays) {
                    List<TimeSlot> slots = eByDay.get(d);
                    if (slots.size() >= 3) {
                        for (int k = 0; k < 3; k++) assignedSlots.add(slots.get(k).getId());
                        return true;
                    }
                }
                // (c) 晚间凑不够 → 全部晚间 + 白天补齐
                for (TimeSlot ts : evening) assignedSlots.add(ts.getId());
                List<TimeSlot> daySlots = avail.stream()
                        .filter(ts -> ts.getSectionNo() < 9)
                        .sorted(Comparator.comparingInt(TimeSlot::getSectionNo))
                        .collect(Collectors.toList());
                for (TimeSlot ts : daySlots) {
                    if (assignedSlots.size() >= needed) break;
                    if (!assignedSlots.contains(ts.getId())) assignedSlots.add(ts.getId());
                }
                // 每天最多3节足够就行
                if (assignedSlots.size() >= needed) {
                    // 去掉多余的(只保留needed个)
                    while (assignedSlots.size() > needed) assignedSlots.remove(assignedSlots.size()-1);
                    return true;
                }
                assignedSlots.clear();
            }
        }

        // ══════════════════ 通用排课 ══════════════════
        // 按天分组
        Map<Integer, List<TimeSlot>> byDay = avail.stream()
                .collect(Collectors.groupingBy(TimeSlot::getWeekday));
        List<Integer> days = new ArrayList<>(byDay.keySet());
        Collections.shuffle(days, new Random());

        // 第1轮：找连续2节 + 同天补齐
        for (int d : days) {
            List<TimeSlot> slots = byDay.get(d);
            slots.sort(Comparator.comparingInt(TimeSlot::getSectionNo));
            for (int i = 0; i + 1 < slots.size(); i++) {
                if (slots.get(i+1).getSectionNo() == slots.get(i).getSectionNo() + 1) {
                    assignedSlots.add(slots.get(i).getId());
                    assignedSlots.add(slots.get(i+1).getId());
                    // 同一天补齐剩余
                    for (TimeSlot ts : slots) {
                        if (assignedSlots.size() >= needed) break;
                        if (!assignedSlots.contains(ts.getId())) assignedSlots.add(ts.getId());
                    }
                    if (assignedSlots.size() >= needed) return true;
                    assignedSlots.clear();
                }
            }
        }

        // 第2轮：同一天凑够（不管是否连续）
        for (int d : days) {
            List<TimeSlot> slots = byDay.get(d);
            if (slots.size() >= needed) {
                for (int i = 0; i < needed; i++) assignedSlots.add(slots.get(i).getId());
                return true;
            }
        }

        // 第3轮：跨天拼凑
        Collections.shuffle(avail, new Random());
        for (TimeSlot ts : avail) {
            if (assignedSlots.size() >= needed) break;
            assignedSlots.add(ts.getId());
        }
        return !assignedSlots.isEmpty();
    }
}
