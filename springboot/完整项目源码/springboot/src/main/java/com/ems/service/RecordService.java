package com.ems.service;

import com.ems.entity.ApprovalRecord;
import com.ems.entity.Schedule;
import com.ems.entity.TeachingTask;

import java.util.List;

public interface RecordService {
    List<TeachingTask> getTeacherTasks(Long teacherId, String semester);
    List<Schedule> getClassSchedules(Long classId, String semester);
    List<Schedule> getTeacherSchedules(Long teacherId, String semester);
    List<TeachingTask> getAllTasks();
    List<Schedule> getAllSchedules(String semester);
    List<String> getAllSemesters();
}
