package com.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ems.entity.*;
import com.ems.mapper.TeachingApplicationMapper;
import com.ems.service.TeachingApplicationService;
import com.ems.service.TeachingPlanService;
import com.ems.service.TeachingRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeachingApplicationServiceImpl implements TeachingApplicationService {

    private static final Logger log = LoggerFactory.getLogger(TeachingApplicationServiceImpl.class);

    @Autowired
    private TeachingApplicationMapper applicationMapper;
    @Autowired
    private TeachingPlanService teachingPlanService;
    @Autowired
    private TeachingRuleService teachingRuleService;

    @Override
    public List<TeachingApplication> list() {
        return applicationMapper.selectAllWithNames();
    }

    @Override
    public List<TeachingApplication> listByCollegeId(Long collegeId) {
        return applicationMapper.selectByCollegeId(collegeId);
    }

    @Override
    public List<TeachingApplication> listByTeacherId(Long teacherId) {
        return applicationMapper.selectByTeacherId(teacherId);
    }

    @Override
    public TeachingApplication getById(Long id) {
        return applicationMapper.selectById(id);
    }

    @Override
    @Transactional
    public TeachingApplication create(TeachingApplication app) {
        TeachingPlan plan = teachingPlanService.getById(app.getTeachingPlanId());
        if (plan == null) throw new IllegalArgumentException("教学计划不存在");
        if (plan.getStatus() < 2) throw new IllegalArgumentException("该计划还未到申报阶段");

        // ★ 提交申报前强制校验：全局规则 + 学院课程级规则
        List<String> ruleErrors = teachingRuleService.validateAll(
                app.getTeacherId(), plan.getCourseId(), plan.getSemester(),
                "主讲", plan.getWeeklyHours());
        if (!ruleErrors.isEmpty()) {
            String msg = "申报校验不通过: " + String.join("; ", ruleErrors);
            log.warn("教师申报被规则拦截: teacherId={}, planId={}, errors={}",
                    app.getTeacherId(), app.getTeachingPlanId(), ruleErrors);
            throw new IllegalArgumentException(msg);
        }

        app.setStatus(1);
        app.setSubmitTime(LocalDateTime.now());
        applicationMapper.insert(app);
        log.info("教师申报成功(已通过全部规则校验): 申报ID={}, 计划ID={}, 教师ID={}",
                app.getId(), app.getTeachingPlanId(), app.getTeacherId());
        return app;
    }

    @Override
    public List<TeachingPlan> getAvailablePlans() {
        List<TeachingPlan> plans = teachingPlanService.list();
        return plans.stream()
                .filter(p -> p.getStatus() != null && p.getStatus() >= 2)
                .toList();
    }
}
