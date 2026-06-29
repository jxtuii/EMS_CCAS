package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.dto.CourseRuleConfigDTO;
import com.ems.entity.*;
import com.ems.mapper.*;
import com.ems.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeachingPlanServiceImpl implements TeachingPlanService {

    private static final Logger log = LoggerFactory.getLogger(TeachingPlanServiceImpl.class);

    @Autowired
    private TeachingPlanMapper teachingPlanMapper;
    @Autowired
    private TeachingRuleMapper teachingRuleMapper;
    @Autowired
    private CollegeService collegeService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private ClazzService clazzService;
    @Autowired
    private TeachingRuleService teachingRuleService;
    @Autowired
    private TeachingApplicationMapper applicationMapper;
    @Autowired
    private ApprovalRecordMapper approvalRecordMapper;
    @Autowired
    private TeachingTaskMapper taskMapper;
    @Autowired
    private ScheduleMapper scheduleMapper;
    @Autowired
    private ExamPlanMapper examPlanMapper;
    @Autowired
    private RoomPlanMapper roomPlanMapper;
    @Autowired
    private TaskPoolMapper taskPoolMapper;

    @Override
    public List<TeachingPlan> list() {
        return teachingPlanMapper.selectAllWithNames();
    }

    @Override
    public List<TeachingPlan> listByCollegeId(Long collegeId) {
        return teachingPlanMapper.selectByCollegeId(collegeId);
    }

    @Override
    public TeachingPlan getById(Long id) {
        return teachingPlanMapper.selectByIdWithNames(id);
    }

    @Override
    public List<TeachingRule> getRules() {
        return teachingRuleMapper.selectList(null);
    }

    @Override
    @Transactional
    public TeachingPlan create(TeachingPlan plan) {
        if (plan.getStatus() == null) plan.setStatus(0);
        teachingPlanMapper.insert(plan);
        log.info("创建教学计划成功: ID={}, 课程ID={}, 班级ID={}", plan.getId(), plan.getCourseId(), plan.getClassId());
        return plan;
    }

    @Override
    @Transactional
    public void publish(Long id) {
        TeachingPlan plan = teachingPlanMapper.selectById(id);
        if (plan == null) throw new IllegalArgumentException("计划不存在");
        plan.setStatus(1);
        teachingPlanMapper.updateById(plan);
        log.info("发布教学计划: ID={}, 状态→待学院配置", id);
    }

    @Override
    @Transactional
    public void batchPublish(List<Long> ids) {
        log.info("批量发布教学计划: 共{}个", ids.size());
        for (Long id : ids) {
            publish(id);
        }
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        TeachingPlan plan = teachingPlanMapper.selectById(id);
        if (plan == null) throw new IllegalArgumentException("计划不存在");
        plan.setStatus(status);
        teachingPlanMapper.updateById(plan);
    }

    @Override
    public List<College> listColleges() {
        return collegeService.list();
    }

    @Override
    @Transactional
    public void saveRules(List<TeachingRule> rules) {
        teachingRuleMapper.delete(null);
        for (TeachingRule rule : rules) {
            if (rule.getId() == null) {
                teachingRuleMapper.insert(rule);
            } else {
                teachingRuleMapper.updateById(rule);
            }
        }
        log.info("全局规则已更新: {} 条", rules.size());
    }

    @Override
    public List<Course> listCourses(Long collegeId) {
        return courseService.listByCollegeId(collegeId);
    }

    @Override
    public List<Clazz> listClasses(Long collegeId) {
        return clazzService.listByCollegeId(collegeId);
    }

    @Override
    @Transactional
    public void configureRules(Long planId, List<String> ruleTypes, List<String> ruleValues) {
        TeachingPlan plan = teachingPlanMapper.selectById(planId);
        if (plan == null) throw new IllegalArgumentException("计划不存在");
        if (plan.getStatus() != 1) throw new IllegalArgumentException("当前状态不允许配置限制");

        teachingRuleMapper.delete(new LambdaQueryWrapper<TeachingRule>()
                .eq(TeachingRule::getRuleName, "PLAN_" + planId));

        if (ruleTypes != null && ruleValues != null) {
            for (int i = 0; i < ruleTypes.size(); i++) {
                TeachingRule rule = new TeachingRule();
                rule.setRuleName("PLAN_" + planId);
                rule.setRuleType(ruleTypes.get(i));
                rule.setRuleValue(ruleValues.get(i));
                rule.setEnabled(1);
                teachingRuleMapper.insert(rule);
            }
        }

        plan.setStatus(2);
        teachingPlanMapper.updateById(plan);
        log.info("教学规则配置完成: 计划ID={}, 规则数量={}, 状态→待教研室协调", planId,
                ruleTypes != null ? ruleTypes.size() : 0);
    }

    // ──────────────────── ★ 学院课程限制条件（新增）★ ────────────────────

    @Override
    @Transactional
    public void configureCourseRules(Long collegeId, List<CourseRuleConfigDTO> configs) {
        if (configs == null || configs.isEmpty()) {
            throw new IllegalArgumentException("配置列表不能为空");
        }

        for (CourseRuleConfigDTO config : configs) {
            String ruleName = "COURSE_" + config.getCourseId();

            // 清除该课程旧的学院级规则
            int deleted = teachingRuleMapper.delete(new LambdaQueryWrapper<TeachingRule>()
                    .eq(TeachingRule::getRuleName, ruleName));
            if (deleted > 0) {
                log.debug("清理课程[ID={}]旧规则: {}条", config.getCourseId(), deleted);
            }

            // ★ 最低职称等级
            if (config.getMinTitleLevel() != null && config.getMinTitleLevel() > 0) {
                TeachingRule rule = new TeachingRule();
                rule.setRuleName(ruleName);
                rule.setRuleType("MIN_TITLE");
                rule.setRuleValue(String.valueOf(config.getMinTitleLevel()));
                rule.setEnabled(1);
                rule.setDescription("学院设置：该课程要求教师最低职称为"
                        + titleLabel(config.getMinTitleLevel()));
                teachingRuleMapper.insert(rule);
            }

            // ★ 最高周学时（教师总计上限）
            if (config.getMaxWeeklyHours() != null && config.getMaxWeeklyHours() > 0) {
                TeachingRule rule = new TeachingRule();
                rule.setRuleName(ruleName);
                rule.setRuleType("MAX_HOURS");
                rule.setRuleValue(String.valueOf(config.getMaxWeeklyHours()));
                rule.setEnabled(1);
                rule.setDescription("学院设置：教师总周学时上限=" + config.getMaxWeeklyHours());
                teachingRuleMapper.insert(rule);
            }

            // ★ 最低周学时（课程至少需要的学时）
            if (config.getMinWeeklyHours() != null && config.getMinWeeklyHours() > 0) {
                TeachingRule rule = new TeachingRule();
                rule.setRuleName(ruleName);
                rule.setRuleType("MIN_HOURS");
                rule.setRuleValue(String.valueOf(config.getMinWeeklyHours()));
                rule.setEnabled(1);
                rule.setDescription("学院设置：课程最低周学时=" + config.getMinWeeklyHours());
                teachingRuleMapper.insert(rule);
            }
        }

        log.info("学院[ID={}]课程限制条件配置完成: {} 门课程", collegeId, configs.size());
    }

    @Override
    public List<CourseRuleConfigDTO> getCourseRuleConfigsByCollegeId(Long collegeId) {
        return teachingRuleService.getCourseRuleConfigsByCollegeId(collegeId);
    }

    private static String titleLabel(int level) {
        return switch (level) {
            case 1 -> "助教";
            case 2 -> "讲师";
            case 3 -> "副教授";
            case 4 -> "教授";
            default -> "等级" + level;
        };
    }

    // ──────────────────── ★ 一键重置（仅教务处管理员）★ ────────────────────

    @Override
    @Transactional
    public void resetAllToDraft() {
        log.warn("========== 教务处管理员执行一键重置：所有教学计划 → 草稿，清空下游数据 ==========");

        // ① 清空所有下游业务表
        int delApp = applicationMapper.delete(null);
        log.info("清空教师申报: {} 条", delApp);

        int delRecord = approvalRecordMapper.delete(null);
        log.info("清空审批记录: {} 条", delRecord);

        int delTask = taskMapper.delete(null);
        log.info("清空教学任务: {} 条", delTask);

        int delSchedule = scheduleMapper.delete(null);
        log.info("清空课表: {} 条", delSchedule);

        int delExam = examPlanMapper.delete(null);
        log.info("清空考试安排: {} 条", delExam);

        int delRoom = roomPlanMapper.delete(null);
        log.info("清空教室安排: {} 条", delRoom);

        int delPool = taskPoolMapper.delete(null);
        log.info("清空任务池: {} 条", delPool);

        // ② 清除学院管理员配置的课程级规则（rule_name 以 COURSE_ 开头）
        int delCourseRules = teachingRuleMapper.delete(
                new LambdaQueryWrapper<TeachingRule>()
                        .likeRight(TeachingRule::getRuleName, "COURSE_"));
        log.info("清空学院课程规则: {} 条", delCourseRules);

        // ③ 清除旧的计划级规则（rule_name 以 PLAN_ 开头）
        int delPlanRules = teachingRuleMapper.delete(
                new LambdaQueryWrapper<TeachingRule>()
                        .likeRight(TeachingRule::getRuleName, "PLAN_"));
        log.info("清空计划级规则: {} 条", delPlanRules);

        // ④ 所有教学计划 status → 0
        List<TeachingPlan> allPlans = teachingPlanMapper.selectList(null);
        int updated = 0;
        for (TeachingPlan plan : allPlans) {
            if (plan.getStatus() != 0) {
                plan.setStatus(0);
                teachingPlanMapper.updateById(plan);
                updated++;
            }
        }
        log.info("重置教学计划状态: {} / {} 个 → status=0(草稿)", updated, allPlans.size());

        log.info("========== 重置完成：{} 个教学计划已回到草稿状态，所有申报/审批/排课数据已清空 ==========",
                allPlans.size());
    }

    @Override
    @Transactional
    public void rollbackScheduling() {
        log.warn("========== 退回排课：status=5 → 4，清空课表 ==========");

        // ① 清空课表（排课结果）
        int delSchedule = scheduleMapper.delete(null);
        log.info("清空课表: {} 条", delSchedule);

        // ② 已发布计划退回待排课
        List<TeachingPlan> publishedPlans = teachingPlanMapper.selectList(
                new LambdaQueryWrapper<TeachingPlan>().eq(TeachingPlan::getStatus, 5));
        for (TeachingPlan plan : publishedPlans) {
            plan.setStatus(4);
            teachingPlanMapper.updateById(plan);
        }
        log.info("退回计划: {} 个 → status=4(待教务处排课)", publishedPlans.size());

        log.info("========== 退回完成：可重新执行排课 ==========");
    }
}
