package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.entity.TeachingApplication;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TeachingApplicationMapper extends BaseMapper<TeachingApplication> {

    @Select("SELECT ta.*, t.teacher_no, u.real_name as teacher_name, " +
            "co.course_name, cl.class_name, tp.semester as plan_semester, tp.total_hours, tp.weekly_hours " +
            "FROM teaching_application ta " +
            "JOIN teacher t ON ta.teacher_id = t.id " +
            "JOIN sys_user u ON t.user_id = u.id " +
            "JOIN teaching_plan tp ON ta.teaching_plan_id = tp.id " +
            "JOIN course co ON tp.course_id = co.id " +
            "JOIN `class` cl ON tp.class_id = cl.id " +
            "ORDER BY ta.id DESC")
    List<TeachingApplication> selectAllWithNames();

    @Select("SELECT ta.*, t.teacher_no, u.real_name as teacher_name, " +
            "co.course_name, cl.class_name, tp.semester as plan_semester, tp.total_hours, tp.weekly_hours " +
            "FROM teaching_application ta " +
            "JOIN teacher t ON ta.teacher_id = t.id " +
            "JOIN sys_user u ON t.user_id = u.id " +
            "JOIN teaching_plan tp ON ta.teaching_plan_id = tp.id " +
            "JOIN course co ON tp.course_id = co.id " +
            "JOIN `class` cl ON tp.class_id = cl.id " +
            "WHERE t.college_id = #{collegeId} " +
            "ORDER BY ta.id DESC")
    List<TeachingApplication> selectByCollegeId(Long collegeId);

    @Select("SELECT ta.*, t.teacher_no, u.real_name as teacher_name, " +
            "co.course_name, cl.class_name, tp.semester as plan_semester, tp.total_hours, tp.weekly_hours " +
            "FROM teaching_application ta " +
            "JOIN teacher t ON ta.teacher_id = t.id " +
            "JOIN sys_user u ON t.user_id = u.id " +
            "JOIN teaching_plan tp ON ta.teaching_plan_id = tp.id " +
            "JOIN course co ON tp.course_id = co.id " +
            "JOIN `class` cl ON tp.class_id = cl.id " +
            "WHERE ta.teacher_id = #{teacherId} " +
            "ORDER BY ta.id DESC")
    List<TeachingApplication> selectByTeacherId(Long teacherId);
}
