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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher/intents")
@Tag(name = "教师端-授课意向")
public class IntentController {

    private static final Logger log = LoggerFactory.getLogger(IntentController.class);

    @Autowired
    private TeachingPlanService teachingPlanService;
    @Autowired
    private TeachingRuleService teachingRuleService;
    @Autowired
    private TeachingApplicationService applicationService;

    @GetMapping("/available-plans")
    @Operation(summary = "获取可申报的教学计划")
    public Result<List<TeachingPlan>> getAvailablePlans() {
        List<TeachingPlan> plans = teachingPlanService.list().stream()
                .filter(p -> p.getStatus() != null && p.getStatus() >= 2)
                .toList();
        return Result.success(plans);
    }

    @PostMapping("/validate")
    @Operation(summary = "校验授课资格(全局规则+学院课程级规则)")
    public Result<List<String>> validateIntent(@AuthenticationPrincipal LoginUser loginUser,
                                                @RequestBody Map<String, Object> params) {
        if (loginUser.getTeacherId() == null) {
            return Result.error("当前用户不是教师");
        }

        @SuppressWarnings("unchecked")
        List<Integer> planIds = (List<Integer>) params.get("planIds");
        String semester = (String) params.getOrDefault("semester", "2025-2026-1");

        List<String> allErrors = new ArrayList<>();
        for (Integer planId : planIds) {
            TeachingPlan plan = teachingPlanService.getById(planId.longValue());
            if (plan == null) continue;
            List<String> errors = teachingRuleService.validateAll(
                    loginUser.getTeacherId(), plan.getCourseId(), semester, "主讲", plan.getWeeklyHours());
            allErrors.addAll(errors);
        }

        return Result.success(allErrors.stream().distinct().toList());
    }

    @PostMapping("/submit")
    @Operation(summary = "提交授课意向（自动校验全局规则+学院课程规则，不通过则拒绝）")
    public Result<Void> submitIntent(@AuthenticationPrincipal LoginUser loginUser,
                                      @RequestBody Map<String, Object> params) {
        if (loginUser.getTeacherId() == null) {
            return Result.error("当前用户不是教师");
        }

        @SuppressWarnings("unchecked")
        List<Integer> planIds = ((List<Integer>) params.get("planIds"));
        String semester = (String) params.getOrDefault("semester", "2025-2026-1");

        log.info("教师[{}]提交授课意向: {} 个计划, 学期={}", loginUser.getTeacherId(), planIds.size(), semester);

        // ★ 提交前预校验：任何一个计划不通过则整体拒绝
        List<String> allErrors = new ArrayList<>();
        for (Integer planId : planIds) {
            TeachingPlan plan = teachingPlanService.getById(planId.longValue());
            if (plan == null) {
                allErrors.add("计划[ID=" + planId + "]不存在");
                continue;
            }
            List<String> errors = teachingRuleService.validateAll(
                    loginUser.getTeacherId(), plan.getCourseId(), semester, "主讲", plan.getWeeklyHours());
            if (!errors.isEmpty()) {
                allErrors.add("[" + plan.getCourseName() + "] " + String.join("; ", errors));
            }
        }
        if (!allErrors.isEmpty()) {
            log.warn("教师[{}]提交意向被规则拦截: {}", loginUser.getTeacherId(), allErrors);
            return Result.error("申报校验不通过: " + String.join(" | ", allErrors));
        }

        // 全部通过 → 创建申报
        for (Integer planId : planIds) {
            TeachingApplication app = new TeachingApplication();
            app.setTeachingPlanId(planId.longValue());
            app.setTeacherId(loginUser.getTeacherId());
            applicationService.create(app); // service层会再次校验，双保险
        }

        log.info("教师[{}]提交授课意向成功: {} 个计划", loginUser.getTeacherId(), planIds.size());
        return Result.success();
    }
}
