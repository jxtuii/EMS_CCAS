package com.ems.service;

import com.ems.dto.ClassScheduleGrid;
import com.ems.dto.TeacherScheduleDTO;

public interface ScheduleExportService {
    /** 教师教学任务书 */
    TeacherScheduleDTO getTeacherSchedule(Long teacherId, String semester);

    /** 班级课表（网格） */
    ClassScheduleGrid getClassScheduleGrid(Long classId, String semester);
}
