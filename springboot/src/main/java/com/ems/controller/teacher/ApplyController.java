package com.ems.controller.teacher;

import com.ems.common.Result;
import com.ems.entity.TeachingApplication;
import com.ems.entity.TeachingPlan;
import com.ems.security.LoginUser;
import com.ems.service.TeachingApplicationService;
import com.ems.service.TeachingPlanService;
import com.ems.service.TeachingRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/applications")
@Tag(name = "教师端-我的申报")
public class ApplyController {

    private static final Logger log = LoggerFactory.getLogger(ApplyController.class);

    @Autowired
    private TeachingApplicationService applicationService;
    @Autowired
    private TeachingPlanService teachingPlanService;
    @Autowired
    private TeachingRuleService teachingRuleService;

    @GetMapping
    @Operation(summary = "获取我的申报列表")
    public Result<List<TeachingApplication>> myApplications(@AuthenticationPrincipal LoginUser loginUser) {
        if (loginUser.getTeacherId() == null) return Result.success(List.of());
        return Result.success(applicationService.listByTeacherId(loginUser.getTeacherId()));
    }

    @PostMapping
    @Operation(summary = "提交申报（自动校验全局规则+学院课程规则，不通过则拒绝）")
    public Result<TeachingApplication> create(@RequestBody TeachingApplication app,
                                              @AuthenticationPrincipal LoginUser loginUser) {
        app.setTeacherId(loginUser.getTeacherId());
        if (app.getTeacherId() == null) throw new IllegalArgumentException("当前用户不是教师");

        // ★ 提交前预校验
        TeachingPlan plan = teachingPlanService.getById(app.getTeachingPlanId());
        if (plan != null) {
            List<String> errors = teachingRuleService.validateAll(
                    loginUser.getTeacherId(), plan.getCourseId(), plan.getSemester(),
                    "主讲", plan.getWeeklyHours());
            if (!errors.isEmpty()) {
                log.warn("教师[{}]申报被规则拦截: planId={}, errors={}",
                        loginUser.getTeacherId(), app.getTeachingPlanId(), errors);
                return Result.error("申报校验不通过: " + String.join("; ", errors));
            }
        }

        log.info("教师[{}]提交申报: 计划ID={}", loginUser.getTeacherId(), app.getTeachingPlanId());
        return Result.success(applicationService.create(app));
    }
}
