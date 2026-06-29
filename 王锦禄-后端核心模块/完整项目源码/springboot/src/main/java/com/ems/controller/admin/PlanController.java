package com.ems.controller.admin;

import com.ems.common.Result;
import com.ems.entity.*;
import com.ems.security.LoginUser;
import com.ems.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/plans")
@Tag(name = "管理端-教学计划")
public class PlanController {

    private static final Logger log = LoggerFactory.getLogger(PlanController.class);

    @Autowired
    private TeachingPlanService teachingPlanService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private ClazzService clazzService;
    @Autowired
    private CollegeService collegeService;

    @GetMapping
    @Operation(summary = "获取教学计划列表")
    public Result<List<TeachingPlan>> list(@AuthenticationPrincipal LoginUser loginUser) {
        if (loginUser.getRoleLevel() <= 1 && loginUser.getCollegeId() != null) {
            return Result.success(teachingPlanService.listByCollegeId(loginUser.getCollegeId()));
        }
        return Result.success(teachingPlanService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取教学计划详情")
    public Result<TeachingPlan> getById(@PathVariable Long id) {
        return Result.success(teachingPlanService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建教学计划")
    public Result<TeachingPlan> create(@RequestBody TeachingPlan plan) {
        log.info("创建教学计划: 课程={}, 班级={}, 学期={}", plan.getCourseId(), plan.getClassId(), plan.getSemester());
        return Result.success(teachingPlanService.create(plan));
    }

    @PutMapping("/{id}/publish")
    @Operation(summary = "发布教学计划")
    public Result<Void> publish(@PathVariable Long id) {
        teachingPlanService.publish(id);
        return Result.success();
    }

    @PutMapping("/batch-publish")
    @Operation(summary = "批量发布教学计划")
    public Result<Void> batchPublish(@RequestBody List<Long> ids) {
        teachingPlanService.batchPublish(ids);
        return Result.success();
    }

    @GetMapping("/rules")
    @Operation(summary = "获取教学规则列表")
    public Result<List<TeachingRule>> getRules() {
        return Result.success(teachingPlanService.getRules());
    }

    @PutMapping("/{id}/rules")
    @Operation(summary = "配置教学规则")
    public Result<Void> configureRules(@PathVariable Long id, @RequestBody java.util.Map<String, java.util.List<String>> params) {
        teachingPlanService.configureRules(id, params.get("ruleTypes"), params.get("ruleValues"));
        return Result.success();
    }

    @PostMapping("/rules/save")
    @Operation(summary = "保存全局规则")
    public Result<Void> saveRules(@RequestBody List<TeachingRule> rules) {
        teachingPlanService.saveRules(rules);
        return Result.success();
    }

    @GetMapping("/colleges")
    @Operation(summary = "获取学院列表")
    public Result<List<College>> listColleges() {
        return Result.success(collegeService.list());
    }

    @GetMapping("/courses")
    @Operation(summary = "获取课程列表")
    public Result<List<Course>> listCourses(@RequestParam(required = false) Long collegeId) {
        if (collegeId != null) return Result.success(courseService.listByCollegeId(collegeId));
        return Result.success(courseService.list());
    }

    @GetMapping("/classes")
    @Operation(summary = "获取班级列表")
    public Result<List<Clazz>> listClasses(@RequestParam(required = false) Long collegeId) {
        if (collegeId != null) return Result.success(clazzService.listByCollegeId(collegeId));
        return Result.success(clazzService.list());
    }

    // ──────────────────── ★ 学院课程限制条件（新增）★ ────────────────────

    @GetMapping("/course-rules")
    @Operation(summary = "获取本学院所有课程的限制条件（用于回显）")
    public Result<List<com.ems.dto.CourseRuleConfigDTO>> getCourseRules(
            @AuthenticationPrincipal LoginUser loginUser) {
        Long collegeId = loginUser.getCollegeId();
        if (collegeId == null) {
            throw new IllegalArgumentException("仅学院管理员可查看课程限制条件");
        }
        return Result.success(teachingPlanService.getCourseRuleConfigsByCollegeId(collegeId));
    }

    @PostMapping("/configure-course-rules")
    @Operation(summary = "学院管理员为本学院所有课程批量设置限制条件")
    public Result<Void> configureCourseRules(
            @RequestBody List<com.ems.dto.CourseRuleConfigDTO> configs,
            @AuthenticationPrincipal LoginUser loginUser) {
        Long collegeId = loginUser.getCollegeId();
        if (collegeId == null) {
            throw new IllegalArgumentException("仅学院管理员可配置课程限制条件，请使用学院管理员账号登录");
        }
        log.info("学院管理员[{}]为本学院[ID={}]配置课程限制条件: {} 门课程",
                loginUser.getUsername(), collegeId, configs.size());
        teachingPlanService.configureCourseRules(collegeId, configs);
        return Result.success();
    }

    @PostMapping("/reset-all")
    @Operation(summary = "一键重置：所有教学计划 → 草稿，清空下游数据（仅教务处管理员）")
    public Result<Void> resetAllToDraft(@AuthenticationPrincipal LoginUser loginUser) {
        if (!loginUser.getRoles().contains("ADMIN_SCHOOL")) {
            return Result.error(403, "仅教务处管理员可执行此操作");
        }
        log.warn("教务处管理员[{}]执行一键重置", loginUser.getUsername());
        teachingPlanService.resetAllToDraft();
        return Result.success();
    }

    @PostMapping("/rollback-scheduling")
    @Operation(summary = "退回排课：status=5→4，清空课表和已发布任务（仅教务处管理员）")
    public Result<Void> rollbackScheduling(@AuthenticationPrincipal LoginUser loginUser) {
        if (!loginUser.getRoles().contains("ADMIN_SCHOOL")) {
            return Result.error(403, "仅教务处管理员可执行此操作");
        }
        log.warn("教务处管理员[{}]退回排课状态", loginUser.getUsername());
        teachingPlanService.rollbackScheduling();
        return Result.success();
    }
}
