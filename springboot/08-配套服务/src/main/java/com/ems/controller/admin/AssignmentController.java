package com.ems.controller.admin;

import com.ems.common.Result;
import com.ems.dto.AssignTeacherDTO;
import com.ems.entity.Teacher;
import com.ems.entity.TeachingTask;
import com.ems.security.LoginUser;
import com.ems.service.AssignmentService;
import com.ems.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/assignments")
@Tag(name = "管理端-教师分配")
public class AssignmentController {

    private static final Logger log = LoggerFactory.getLogger(AssignmentController.class);

    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private TeacherService teacherService;

    @GetMapping("/teachers")
    @Operation(summary = "获取教师列表(供分配选择)")
    public Result<List<Teacher>> listTeachers() {
        return Result.success(teacherService.list());
    }

    @PostMapping("/validate")
    @Operation(summary = "校验分配规则")
    public Result<List<String>> validate(@RequestBody AssignTeacherDTO dto) {
        return Result.success(assignmentService.validateRules(dto));
    }

    @PostMapping
    @Operation(summary = "执行教师分配")
    public Result<TeachingTask> assign(@RequestBody AssignTeacherDTO dto) {
        log.info("执行教师分配: 申请ID={}, 教师ID={}", dto.getApplicationId(), dto.getTeacherId());
        return Result.success(assignmentService.assign(dto));
    }
}
