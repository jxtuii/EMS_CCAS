package com.ems.service;

import com.ems.dto.CourseRuleConfigDTO;
import com.ems.entity.*;

import java.util.List;

public interface TeachingPlanService {
    List<TeachingPlan> list();
    List<TeachingPlan> listByCollegeId(Long collegeId);
    TeachingPlan getById(Long id);
    List<TeachingRule> getRules();
    TeachingPlan create(TeachingPlan plan);
    void publish(Long id);
    void batchPublish(List<Long> ids);
    void updateStatus(Long id, Integer status);
    List<College> listColleges();
    List<Course> listCourses(Long collegeId);
    List<Clazz> listClasses(Long collegeId);
    void configureRules(Long planId, List<String> ruleTypes, List<String> ruleValues);
    void saveRules(List<TeachingRule> rules);

    /** 学院管理员为本学院所有课程批量配置限制条件（职称、周学时上下限） */
    void configureCourseRules(Long collegeId, List<CourseRuleConfigDTO> configs);

    /** 获取学院所有课程已有的限制条件（用于前端回显） */
    List<CourseRuleConfigDTO> getCourseRuleConfigsByCollegeId(Long collegeId);

    /** 一键重置：所有教学计划 status→0，清空所有下游业务数据 */
    void resetAllToDraft();

    /** 退回排课：status=5→4，清空课表和已发布的任务 */
    void rollbackScheduling();
}
