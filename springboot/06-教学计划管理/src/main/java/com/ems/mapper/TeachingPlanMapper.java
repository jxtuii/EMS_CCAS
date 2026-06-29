package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.entity.TeachingPlan;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TeachingPlanMapper extends BaseMapper<TeachingPlan> {

    @Select("SELECT tp.*, c.name as college_name, co.course_name, cl.class_name, u.real_name " +
            "FROM teaching_plan tp " +
            "LEFT JOIN college c ON tp.college_id = c.id " +
            "LEFT JOIN course co ON tp.course_id = co.id " +
            "LEFT JOIN `class` cl ON tp.class_id = cl.id " +
            "LEFT JOIN sys_user u ON tp.create_by = u.id " +
            "ORDER BY tp.id DESC")
    List<TeachingPlan> selectAllWithNames();

    @Select("SELECT tp.*, c.name as college_name, co.course_name, cl.class_name, u.real_name " +
            "FROM teaching_plan tp " +
            "LEFT JOIN college c ON tp.college_id = c.id " +
            "LEFT JOIN course co ON tp.course_id = co.id " +
            "LEFT JOIN `class` cl ON tp.class_id = cl.id " +
            "LEFT JOIN sys_user u ON tp.create_by = u.id " +
            "WHERE tp.college_id = #{collegeId} " +
            "ORDER BY tp.id DESC")
    List<TeachingPlan> selectByCollegeId(Long collegeId);

    @Select("SELECT tp.*, c.name as college_name, co.course_name, cl.class_name, u.real_name " +
            "FROM teaching_plan tp " +
            "LEFT JOIN college c ON tp.college_id = c.id " +
            "LEFT JOIN course co ON tp.course_id = co.id " +
            "LEFT JOIN `class` cl ON tp.class_id = cl.id " +
            "LEFT JOIN sys_user u ON tp.create_by = u.id " +
            "WHERE tp.id = #{id}")
    TeachingPlan selectByIdWithNames(Long id);
}
