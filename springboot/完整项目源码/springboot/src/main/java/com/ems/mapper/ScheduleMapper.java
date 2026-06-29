package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.entity.Schedule;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ScheduleMapper extends BaseMapper<Schedule> {

    @Select("SELECT s.*, cl.class_name, co.course_name, u.real_name as teacher_name, " +
            "ts.weekday, ts.section_no, ts.start_time, ts.end_time " +
            "FROM schedule s " +
            "LEFT JOIN `class` cl ON s.class_id = cl.id " +
            "LEFT JOIN course co ON s.course_id = co.id " +
            "LEFT JOIN teacher t ON s.teacher_id = t.id " +
            "LEFT JOIN sys_user u ON t.user_id = u.id " +
            "LEFT JOIN time_slot ts ON s.time_slot_id = ts.id " +
            "WHERE s.class_id = #{classId} AND s.semester = #{semester} " +
            "ORDER BY ts.weekday, ts.section_no")
    List<Schedule> selectByClassAndSemester(Long classId, String semester);

    @Select("SELECT s.*, cl.class_name, co.course_name, u.real_name as teacher_name, " +
            "ts.weekday, ts.section_no, ts.start_time, ts.end_time " +
            "FROM schedule s " +
            "LEFT JOIN `class` cl ON s.class_id = cl.id " +
            "LEFT JOIN course co ON s.course_id = co.id " +
            "LEFT JOIN teacher t ON s.teacher_id = t.id " +
            "LEFT JOIN sys_user u ON t.user_id = u.id " +
            "LEFT JOIN time_slot ts ON s.time_slot_id = ts.id " +
            "WHERE s.teacher_id = #{teacherId} AND s.semester = #{semester} " +
            "ORDER BY ts.weekday, ts.section_no")
    List<Schedule> selectByTeacherAndSemester(Long teacherId, String semester);

    @Select("SELECT s.*, cl.class_name, co.course_name, u.real_name as teacher_name, " +
            "ts.weekday, ts.section_no, ts.start_time, ts.end_time " +
            "FROM schedule s " +
            "LEFT JOIN `class` cl ON s.class_id = cl.id " +
            "LEFT JOIN course co ON s.course_id = co.id " +
            "LEFT JOIN teacher t ON s.teacher_id = t.id " +
            "LEFT JOIN sys_user u ON t.user_id = u.id " +
            "LEFT JOIN time_slot ts ON s.time_slot_id = ts.id " +
            "WHERE s.semester = #{semester} " +
            "ORDER BY ts.weekday, ts.section_no")
    List<Schedule> selectBySemester(String semester);

    @Select("SELECT s.*, cl.class_name, co.course_name, u.real_name as teacher_name, " +
            "ts.weekday, ts.section_no, ts.start_time, ts.end_time " +
            "FROM schedule s " +
            "LEFT JOIN `class` cl ON s.class_id = cl.id " +
            "LEFT JOIN course co ON s.course_id = co.id " +
            "LEFT JOIN teacher t ON s.teacher_id = t.id " +
            "LEFT JOIN sys_user u ON t.user_id = u.id " +
            "LEFT JOIN time_slot ts ON s.time_slot_id = ts.id " +
            "WHERE s.id = #{id}")
    Schedule selectByIdWithNames(Long id);
}
