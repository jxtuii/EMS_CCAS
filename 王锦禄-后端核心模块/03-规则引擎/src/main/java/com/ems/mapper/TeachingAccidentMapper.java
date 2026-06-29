package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.entity.TeachingAccident;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TeachingAccidentMapper extends BaseMapper<TeachingAccident> {

    @Select("SELECT * FROM teaching_accident WHERE teacher_id = #{teacherId} AND semester = #{semester} AND is_serious = 1")
    List<TeachingAccident> selectSeriousByTeacherAndSemester(Long teacherId, String semester);

    @Select("SELECT COUNT(*) FROM teaching_accident WHERE teacher_id = #{teacherId} AND semester = #{semester} AND is_serious = 1")
    int countSeriousByTeacherAndSemester(Long teacherId, String semester);
}
