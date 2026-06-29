package com.ems.controller.admin;

import com.ems.common.Result;
import com.ems.dto.ApprovalDTO;
import com.ems.entity.ApprovalRecord;
import com.ems.security.LoginUser;
import com.ems.service.ApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/approvals")
@Tag(name = "管理端-审核管理")
public class ApprovalController {

    private static final Logger log = LoggerFactory.getLogger(ApprovalController.class);

    @Autowired
    private ApprovalService approvalService;

    @PostMapping
    @Operation(summary = "审核(通过/驳回)")
    public Result<Void> approve(@RequestBody ApprovalDTO dto,
                                @AuthenticationPrincipal LoginUser loginUser) {
        // 按角色优先级判定：教研室主任 > 学院管理员（兼有教研室主任身份的学院管理员以学院管理员身份审批）
        String role;
        boolean isDept = loginUser.getRoles().contains("DIRECTOR_DEPT");
        boolean isCollegeAdmin = loginUser.getRoles().contains("ADMIN_COLLEGE");
        boolean isSchoolAdmin = loginUser.getRoles().contains("ADMIN_SCHOOL");
        if (isDept && !isCollegeAdmin) {
            role = "教研室主任";
        } else {
            role = "学院管理员"; // ADMIN_COLLEGE 或 ADMIN_SCHOOL 均以学院管理员身份审批
        }
        log.info("审核: 申请ID={}, 操作={}, 审核人ID={}", dto.getApplicationId(),
                dto.getApproved() ? "通过" : "驳回", loginUser.getUserId());
        approvalService.approve(dto, loginUser.getUserId(), role);
        return Result.success();
    }

    @GetMapping("/records/{applicationId}")
    @Operation(summary = "获取审核记录")
    public Result<List<ApprovalRecord>> getRecords(@PathVariable Long applicationId) {
        return Result.success(approvalService.getRecordsByApplicationId(applicationId));
    }
}
