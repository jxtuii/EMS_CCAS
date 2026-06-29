package com.ems.dto;

import lombok.Data;
import java.util.List;

/**
 * 教师完整教学任务书
 */
@Data
public class TeacherScheduleDTO {
    private String teacherName;
    private String semester;
    private List<TeacherScheduleItem> tasks;
}
