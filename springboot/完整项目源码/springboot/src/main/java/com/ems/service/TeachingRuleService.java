package com.ems.service;

import com.ems.dto.CourseRuleConfigDTO;
import com.ems.entity.TeachingRule;

import java.util.List;

public interface TeachingRuleService {
    List<String> validateAll(Long teacherId, Long courseId, String semester, String roleType, Integer weeklyHours);
    List<String> validateTeacher(Long teacherId, String semester, String roleType);
    List<TeachingRule> getEnabledRules();
    boolean isTeacherBanned(Long teacherId, String semester);

    /** 获取指定课程的所有学院级限制规则 */
    List<TeachingRule> getCourseRules(Long courseId);

    /** 校验教师是否满足课程的学院级限制条件（职称、周学时上下限） */
    List<String> validateCourseRules(Long teacherId, Long courseId, String semester, Integer weeklyHours);

    /** 获取学院所有课程的现有规则配置（用于前端回显） */
    List<CourseRuleConfigDTO> getCourseRuleConfigsByCollegeId(Long collegeId);

    /** 校验规则（排除指定申请ID，避免审批/分配时自身重复计数） */
    List<String> validateAllExcluding(Long teacherId, Long courseId, String semester,
                                       String roleType, Integer weeklyHours, Long excludeApplicationId);
}
