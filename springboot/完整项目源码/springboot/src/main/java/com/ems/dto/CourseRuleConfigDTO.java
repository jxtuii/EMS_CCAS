package com.ems.dto;

import lombok.Data;

/**
 * 学院管理员为本学院课程设置的限制条件
 */
@Data
public class CourseRuleConfigDTO {
    /** 课程ID */
    private Long courseId;
    /** 课程名称（回显用） */
    private String courseName;
    /** 最低职称等级：1-助教 2-讲师 3-副教授 4-教授 */
    private Integer minTitleLevel;
    /** 教师周学时上限（含本课程） */
    private Integer maxWeeklyHours;
    /** 课程最低周学时 */
    private Integer minWeeklyHours;
}
