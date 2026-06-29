package com.ems.service.impl;

import com.ems.dto.*;
import com.ems.entity.*;
import com.ems.mapper.*;
import com.ems.service.ScheduleExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleExportServiceImpl implements ScheduleExportService {

    @Autowired
    private ScheduleMapper scheduleMapper;
    @Autowired
    private ClazzMapper clazzMapper;

    private static final String[] WEEK_LABELS = {"", "周一", "周二", "周三", "周四", "周五"};
    private static final int MAX_SECTION = 11; // 上午4 + 下午4 + 晚上3

    // ────────────────── 教师教学任务书 ──────────────────

    @Override
    public TeacherScheduleDTO getTeacherSchedule(Long teacherId, String semester) {
        TeacherScheduleDTO dto = new TeacherScheduleDTO();

        List<Schedule> records = scheduleMapper.selectByTeacherAndSemester(teacherId, semester);
        if (records.isEmpty()) {
            dto.setTeacherName("教师ID=" + teacherId);
            dto.setSemester(semester);
            dto.setTasks(List.of());
            return dto;
        }

        dto.setTeacherName(records.get(0).getTeacherName());
        dto.setSemester(semester);

        Map<String, List<Schedule>> grouped = records.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getCourseId() + "_" + s.getClassId(),
                        LinkedHashMap::new, Collectors.toList()));

        List<TeacherScheduleItem> tasks = new ArrayList<>();
        for (List<Schedule> group : grouped.values()) {
            Schedule first = group.get(0);
            TeacherScheduleItem item = new TeacherScheduleItem();
            item.setCourseName(first.getCourseName());
            item.setClassName(first.getClassName());
            item.setWeeklyHours(group.size());
            item.setTimeDescriptions(mergeTimeDescriptions(group));
            tasks.add(item);
        }

        dto.setTasks(tasks);
        return dto;
    }

    private List<String> mergeTimeDescriptions(List<Schedule> slots) {
        Map<Integer, List<Schedule>> byDay = slots.stream()
                .collect(Collectors.groupingBy(Schedule::getWeekday));
        List<String> result = new ArrayList<>();

        for (int w = 1; w <= 5; w++) {
            List<Schedule> daySlots = byDay.get(w);
            if (daySlots == null || daySlots.isEmpty()) continue;
            daySlots.sort(Comparator.comparingInt(Schedule::getSectionNo));

            List<String> parts = new ArrayList<>();
            int i = 0;
            while (i < daySlots.size()) {
                Schedule segStart = daySlots.get(i);
                int endNo = segStart.getSectionNo();
                String endTime = segStart.getEndTime();
                int j = i + 1;
                while (j < daySlots.size()
                        && daySlots.get(j).getSectionNo() == endNo + 1) {
                    endNo = daySlots.get(j).getSectionNo();
                    endTime = daySlots.get(j).getEndTime();
                    j++;
                }
                if (endNo == segStart.getSectionNo()) {
                    parts.add("第" + segStart.getSectionNo() + "节 "
                            + segStart.getStartTime() + "-" + segStart.getEndTime());
                } else {
                    parts.add("第" + segStart.getSectionNo() + "-" + endNo + "节 "
                            + segStart.getStartTime() + "-" + endTime);
                }
                i = j;
            }
            result.add(WEEK_LABELS[w] + " " + String.join("；", parts));
        }
        return result;
    }

    // ────────────────── 班级课表网格 ──────────────────

    @Override
    public ClassScheduleGrid getClassScheduleGrid(Long classId, String semester) {
        ClassScheduleGrid grid = new ClassScheduleGrid();

        com.ems.entity.Clazz clazz = clazzMapper.selectById(classId);
        grid.setClassName(clazz != null ? clazz.getClassName() : "ID=" + classId);
        grid.setSemester(semester);
        grid.setWeekLabels(List.of("周一", "周二", "周三", "周四", "周五"));

        List<Integer> secList = new ArrayList<>();
        for (int i = 1; i <= MAX_SECTION; i++) secList.add(i);
        grid.setSections(secList);

        List<Schedule> records = scheduleMapper.selectByClassAndSemester(classId, semester);
        if (records.isEmpty()) return grid;

        Map<Integer, Map<Integer, Schedule>> index = new LinkedHashMap<>();
        for (Schedule s : records) {
            index.computeIfAbsent(s.getWeekday(), k -> new LinkedHashMap<>())
                    .put(s.getSectionNo(), s);
        }

        for (int w = 1; w <= 5; w++) {
            Map<Integer, GridCell> row = new LinkedHashMap<>();
            Map<Integer, Schedule> day = index.getOrDefault(w, Map.of());

            for (int sec = 1; sec <= MAX_SECTION; sec++) {
                if (row.containsKey(sec)) continue;
                Schedule s = day.get(sec);
                if (s == null) {
                    row.put(sec, null);
                    continue;
                }
                int span = 1;
                while (day.containsKey(sec + span) && sameCourse(day.get(sec + span), s)) {
                    span++;
                }
                GridCell cell = new GridCell();
                cell.setCourseName(s.getCourseName());
                cell.setTeacherName(s.getTeacherName());
                cell.setSectionSpan(span);
                row.put(sec, cell);
                for (int k = 1; k < span; k++) {
                    row.put(sec + k, new GridCell());
                }
            }
            grid.getGrid().put(w, row);
        }

        return grid;
    }

    private boolean sameCourse(Schedule a, Schedule b) {
        return Objects.equals(a.getCourseId(), b.getCourseId())
                && Objects.equals(a.getClassId(), b.getClassId());
    }
}
