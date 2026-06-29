package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.dto.ApprovalDTO;
import com.ems.entity.*;
import com.ems.mapper.*;
import com.ems.service.ApprovalService;
import com.ems.service.TeachingRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ApprovalServiceImpl implements ApprovalService {

    private static final Logger log = LoggerFactory.getLogger(ApprovalServiceImpl.class);

    @Autowired
    private TeachingApplicationMapper applicationMapper;
    @Autowired
    private ApprovalRecordMapper approvalRecordMapper;
    @Autowired
    private TeachingPlanMapper planMapper;
    @Autowired
    private TeachingTaskMapper taskMapper;
    @Autowired
    private TeachingRuleMapper ruleMapper;
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private TeachingRuleService teachingRuleService;

    // ── 读全局规则值 ──
    private int getRuleInt(String ruleType, int defaultVal) {
        List<TeachingRule> rules = ruleMapper.selectList(
                new LambdaQueryWrapper<TeachingRule>()
                        .eq(TeachingRule::getRuleType, ruleType)
                        .eq(TeachingRule::getEnabled, 1));
        if (rules != null && !rules.isEmpty()) {
            try { return Integer.parseInt(rules.get(0).getRuleValue()); }
            catch (NumberFormatException ignored) {}
        }
        return defaultVal;
    }

    @Override
    @Transactional
    public void approve(ApprovalDTO dto, Long approverId, String approverRole) {
        TeachingApplication app = applicationMapper.selectById(dto.getApplicationId());
        if (app == null) throw new IllegalArgumentException("申请不存在");

        // ── ★ 审批通过前重新校验规则（防教师在申报→审批之间又申报其他课导致超限）──
        if (dto.getApproved()) {
            TeachingPlan plan = planMapper.selectById(app.getTeachingPlanId());
            if (plan != null) {
                revalidateBeforeApproval(app, plan);
            }
        }

        // ── 第一步：更新申请状态并立即持久化 ──
        if (dto.getApproved()) {
            if ("教研室主任".equals(approverRole)) {
                app.setStatus(2);
            } else if ("学院管理员".equals(approverRole)) {
                app.setStatus(4);
            }
        } else {
            if ("教研室主任".equals(approverRole)) {
                app.setStatus(3);
            } else if ("学院管理员".equals(approverRole)) {
                app.setStatus(5);
            }
        }
        applicationMapper.updateById(app);

        // ── 第二步：创建审批记录 ──
        ApprovalRecord record = new ApprovalRecord();
        record.setApplicationId(dto.getApplicationId());
        record.setApproverId(approverId);
        record.setApproverRole(approverRole);
        record.setAction(dto.getApproved() ? "通过" : "驳回");
        record.setComment(dto.getComment());
        record.setCreateTime(LocalDateTime.now());
        approvalRecordMapper.insert(record);

        log.info("审核完成: 申请[ID={}] {} → status={}, 审核人={}({})",
                dto.getApplicationId(),
                dto.getApproved() ? "通过" : "驳回",
                app.getStatus(), approverId, approverRole);

        // ── 第三步：学院管理员通过后，更新教学计划状态 ──
        if (dto.getApproved() && "学院管理员".equals(approverRole)) {
            TeachingPlan plan = planMapper.selectById(app.getTeachingPlanId());
            if (plan != null) {
                // 去重创建教学任务
                List<TeachingTask> existingTasks = taskMapper.selectList(
                        new LambdaQueryWrapper<TeachingTask>()
                                .eq(TeachingTask::getTeacherId, app.getTeacherId())
                                .eq(TeachingTask::getCourseId, plan.getCourseId())
                                .eq(TeachingTask::getClassId, plan.getClassId())
                                .eq(TeachingTask::getSemester, plan.getSemester()));
                if (existingTasks.isEmpty()) {
                    TeachingTask task = new TeachingTask();
                    task.setTeacherId(app.getTeacherId());
                    task.setCourseId(plan.getCourseId());
                    task.setClassId(plan.getClassId());
                    task.setSemester(plan.getSemester());
                    task.setRoleType("主讲");
                    task.setTotalHours(plan.getTotalHours());
                    task.setWeeklyHours(plan.getWeeklyHours());
                    task.setStatus(1);
                    taskMapper.insert(task);
                    log.info("学院审核通过，自动创建教学任务[ID={}]: 教师ID={}, 课程ID={}",
                            task.getId(), app.getTeacherId(), plan.getCourseId());
                }

                // 判断计划是否可推进到待排课
                List<TeachingApplication> apps = applicationMapper.selectList(
                        new LambdaQueryWrapper<TeachingApplication>()
                                .eq(TeachingApplication::getTeachingPlanId, plan.getId()));
                boolean allSubmittedResolved = apps.stream()
                        .filter(a -> a.getStatus() != 0)
                        .allMatch(a -> a.getStatus() == 4 || a.getStatus() == 5);
                boolean anyApproved = apps.stream().anyMatch(a -> a.getStatus() == 4);

                if (allSubmittedResolved && anyApproved) {
                    plan.setStatus(4);
                    planMapper.updateById(plan);
                    log.info("教学计划[ID={}]全部申请已处理完成，状态→待教务处排课(status=4)", plan.getId());
                } else {
                    plan.setStatus(3);
                    planMapper.updateById(plan);
                    log.info("教学计划[ID={}]尚有申请未终态，状态→待学院审核(status=3)", plan.getId());
                }
            }
        }
    }

    /**
     * 审批前重新校验全局规则（模拟审批通过后的状态）。
     * 排除当前申请自身的学时，防止重复计数。
     */
    private void revalidateBeforeApproval(TeachingApplication app, TeachingPlan plan) {
        Long teacherId = app.getTeacherId();
        String semester = plan.getSemester();
        int thisWeeklyHours = plan.getWeeklyHours() != null ? plan.getWeeklyHours() : 0;

        // ── 已创建的教学任务（排除与当前申请同一课程+班级的任务）──
        List<TeachingTask> tasks = taskMapper.selectByTeacherAndSemester(teacherId, semester);
        int taskHours = 0;
        Set<Long> taskCourseIds = new HashSet<>();
        // 收集任务已覆盖的 (课程,班级)，用于去重
        Set<String> taskCovered = new HashSet<>();
        for (TeachingTask t : tasks) {
            if (plan.getCourseId() != null && plan.getCourseId().equals(t.getCourseId())
                    && plan.getClassId() != null && plan.getClassId().equals(t.getClassId())) {
                continue; // 本申请对应的任务，跳过
            }
            if (t.getWeeklyHours() != null) taskHours += t.getWeeklyHours();
            if (t.getCourseId() != null) taskCourseIds.add(t.getCourseId());
            if (t.getCourseId() != null && t.getClassId() != null)
                taskCovered.add(t.getCourseId() + "_" + t.getClassId());
        }

        // ── 其他待审批申报（排除当前申请 + 排除已被任务覆盖的课程）──
        List<TeachingApplication> otherApps = applicationMapper.selectList(
                new LambdaQueryWrapper<TeachingApplication>()
                        .eq(TeachingApplication::getTeacherId, teacherId)
                        .in(TeachingApplication::getStatus, 1, 2)
                        .ne(TeachingApplication::getId, app.getId()));
        int otherAppHours = 0;
        Set<Long> otherAppCourseIds = new HashSet<>();
        for (TeachingApplication oa : otherApps) {
            TeachingPlan p = planMapper.selectById(oa.getTeachingPlanId());
            if (p != null && semester.equals(p.getSemester())) {
                String key = p.getCourseId() + "_" + p.getClassId();
                if (taskCovered.contains(key)) continue; // 该课已有任务，不重复计数
                if (p.getWeeklyHours() != null) otherAppHours += p.getWeeklyHours();
                if (p.getCourseId() != null) otherAppCourseIds.add(p.getCourseId());
            }
        }

        int projectedHours = taskHours + otherAppHours + thisWeeklyHours;
        int projectedCourses = taskCourseIds.size() + otherAppCourseIds.size() + 1; // +1=本申请

        // ── 规则①：主讲门数上限 ──
        int maxCourses = getRuleInt("MAX_COURSE_LIMIT", 2);
        if (projectedCourses > maxCourses) {
            throw new IllegalArgumentException(
                    "审批不通过：规则① — 该教师已有 " + taskCourseIds.size() + " 门已排课程 + "
                            + otherAppCourseIds.size() + " 门待审批申报，审批通过后主讲课程将达 "
                            + projectedCourses + " 门，超过上限 " + maxCourses + " 门");
        }

        // ── 规则③：中层干部周学时 ──
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher != null && teacher.getIsMiddleLeader() != null && teacher.getIsMiddleLeader() == 1) {
            int maxHours = getRuleInt("LEADER_HOUR_LIMIT", 4);
            if (projectedHours > maxHours) {
                throw new IllegalArgumentException(
                        "审批不通过：规则③ — 该教师为中层干部，已排 " + taskHours + " 学时 + "
                                + otherAppHours + " 学时(待审批)，审批通过后周总学时将达 "
                                + projectedHours + " 学时，超过上限 " + maxHours + " 学时");
            }
        }
    }

    @Override
    public List<ApprovalRecord> getRecordsByApplicationId(Long applicationId) {
        return approvalRecordMapper.selectByApplicationId(applicationId);
    }
}
