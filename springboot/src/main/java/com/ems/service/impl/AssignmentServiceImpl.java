package com.ems.service.impl;

import com.ems.dto.AssignTeacherDTO;
import com.ems.entity.*;
import com.ems.mapper.*;
import com.ems.service.AssignmentService;
import com.ems.service.TeachingRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private static final Logger log = LoggerFactory.getLogger(AssignmentServiceImpl.class);

    @Autowired
    private TeachingApplicationMapper applicationMapper;
    @Autowired
    private TeachingTaskMapper taskMapper;
    @Autowired
    private TeachingPlanMapper planMapper;
    @Autowired
    private TeachingRuleService teachingRuleService;

    @Override
    public List<String> validateRules(AssignTeacherDTO dto) {
        log.debug("开始校验教师分配规则: 申请ID={}, 教师ID={}", dto.getApplicationId(), dto.getTeacherId());

        TeachingApplication app = applicationMapper.selectById(dto.getApplicationId());
        if (app == null) throw new IllegalArgumentException("申请不存在");

        TeachingPlan plan = planMapper.selectById(app.getTeachingPlanId());
        if (plan == null) throw new IllegalArgumentException("教学计划不存在");

        // ★ 统一入口：全局规则(读DB) + 学院课程规则(读DB)
        // 传入 applicationId 排除自身，避免重复计数
        return teachingRuleService.validateAllExcluding(
                dto.getTeacherId(),
                plan.getCourseId(),
                plan.getSemester(),
                dto.getRoleType() != null ? dto.getRoleType() : "主讲",
                plan.getWeeklyHours(),
                dto.getApplicationId());
    }

    @Override
    @Transactional
    public TeachingTask assign(AssignTeacherDTO dto) {
        List<String> errors = validateRules(dto);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("分配校验失败: " + String.join("; ", errors));
        }

        TeachingApplication app = applicationMapper.selectById(dto.getApplicationId());
        TeachingPlan plan = planMapper.selectById(app.getTeachingPlanId());

        TeachingTask task = new TeachingTask();
        task.setTeacherId(dto.getTeacherId());
        task.setCourseId(plan.getCourseId());
        task.setClassId(plan.getClassId());
        task.setSemester(plan.getSemester());
        task.setRoleType(dto.getRoleType() != null ? dto.getRoleType() : "主讲");
        task.setTotalHours(plan.getTotalHours());
        task.setWeeklyHours(plan.getWeeklyHours());
        task.setStatus(1);
        taskMapper.insert(task);

        app.setStatus(2);
        applicationMapper.updateById(app);

        log.info("教师分配成功(已通过全部规则校验): 任务ID={}, 教师ID={}, 课程ID={}, 班级ID={}",
                task.getId(), task.getTeacherId(), task.getCourseId(), task.getClassId());
        return task;
    }
}
