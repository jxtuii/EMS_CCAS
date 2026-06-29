package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.entity.TeachingTask;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TeachingTaskMapper extends BaseMapper<TeachingTask> {

    @Select("SELECT tt.*, u.real_name as teacher_name, t.teacher_no, " +
            "co.course_name, cl.class_name " +
            "FROM teaching_task tt " +
            "LEFT JOIN teacher t ON tt.teacher_id = t.id " +
            "LEFT JOIN sys_user u ON t.user_id = u.id " +
            "LEFT JOIN course co ON tt.course_id = co.id " +
            "LEFT JOIN `class` cl ON tt.class_id = cl.id " +
            "WHERE tt.semester = #{semester} " +
            "ORDER BY tt.id")
    List<TeachingTask> selectBySemester(String semester);

    @Select("SELECT tt.*, u.real_name as teacher_name, t.teacher_no, " +
            "co.course_name, cl.class_name " +
            "FROM teaching_task tt " +
            "LEFT JOIN teacher t ON tt.teacher_id = t.id " +
            "LEFT JOIN sys_user u ON t.user_id = u.id " +
            "LEFT JOIN course co ON tt.course_id = co.id " +
            "LEFT JOIN `class` cl ON tt.class_id = cl.id " +
            "WHERE tt.teacher_id = #{teacherId} AND tt.semester = #{semester} " +
            "ORDER BY tt.id")
    List<TeachingTask> selectByTeacherAndSemester(Long teacherId, String semester);

    @Select("SELECT tt.*, u.real_name as teacher_name, t.teacher_no, " +
            "co.course_name, cl.class_name " +
            "FROM teaching_task tt " +
            "LEFT JOIN teacher t ON tt.teacher_id = t.id " +
            "LEFT JOIN sys_user u ON t.user_id = u.id " +
            "LEFT JOIN course co ON tt.course_id = co.id " +
            "LEFT JOIN `class` cl ON tt.class_id = cl.id " +
            "WHERE tt.class_id = #{classId} AND tt.semester = #{semester} " +
            "ORDER BY tt.id")
    List<TeachingTask> selectByClassAndSemester(Long classId, String semester);

    @Select("SELECT COALESCE(SUM(tt.total_hours), 0) FROM teaching_task tt " +
            "WHERE tt.teacher_id = #{teacherId} AND tt.semester = #{semester}")
    Integer sumHoursByTeacherAndSemester(Long teacherId, String semester);

    @Select("SELECT COUNT(*) FROM teaching_task tt " +
            "WHERE tt.teacher_id = #{teacherId} AND tt.semester = #{semester} " +
            "AND tt.role_type = '主讲'")
    Integer countMainByTeacherAndSemester(Long teacherId, String semester);

    @Select("SELECT tt.*, u.real_name as teacher_name, t.teacher_no, " +
            "co.course_name, cl.class_name " +
            "FROM teaching_task tt " +
            "LEFT JOIN teacher t ON tt.teacher_id = t.id " +
            "LEFT JOIN sys_user u ON t.user_id = u.id " +
            "LEFT JOIN course co ON tt.course_id = co.id " +
            "LEFT JOIN `class` cl ON tt.class_id = cl.id " +
            "ORDER BY tt.id DESC")
    List<TeachingTask> selectAllWithNames();
}
