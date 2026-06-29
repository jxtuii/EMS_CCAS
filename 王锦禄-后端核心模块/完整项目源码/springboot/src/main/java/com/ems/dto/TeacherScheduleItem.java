package com.ems.dto;

import lombok.Data;
import java.util.List;

/**
 * 教师教学任务书 — 单个任务项
 */
@Data
public class TeacherScheduleItem {
    private String courseName;
    private String className;
    private Integer totalHours;
    private Integer weeklyHours;
    private List<String> timeDescriptions;
}
