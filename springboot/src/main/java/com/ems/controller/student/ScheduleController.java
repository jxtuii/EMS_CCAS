package com.ems.controller.student;

import com.ems.common.Result;
import com.ems.dto.ClassScheduleGrid;
import com.ems.entity.Schedule;
import com.ems.security.LoginUser;
import com.ems.service.RecordService;
import com.ems.service.ScheduleExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("studentScheduleController")
@RequestMapping("/api/student/schedules")
@Tag(name = "学生端-课表查询")
public class ScheduleController {

    @Autowired
    private RecordService recordService;
    @Autowired
    private ScheduleExportService scheduleExportService;

    @GetMapping
    @Operation(summary = "获取班级课表（原始数据）")
    public Result<List<Schedule>> classSchedule(@AuthenticationPrincipal LoginUser loginUser,
                                                 @RequestParam(defaultValue = "2025-2026-1") String semester) {
        if (loginUser.getClassId() == null) {
            return Result.error("未找到该学生的班级信息");
        }
        return Result.success(recordService.getClassSchedules(loginUser.getClassId(), semester));
    }

    @GetMapping("/grid")
    @Operation(summary = "获取班级课表（网格格式，含连续节次合并）")
    public Result<ClassScheduleGrid> classScheduleGrid(@AuthenticationPrincipal LoginUser loginUser,
                                                        @RequestParam(defaultValue = "2025-2026-2") String semester) {
        if (loginUser.getClassId() == null) {
            return Result.error("未找到该学生的班级信息");
        }
        return Result.success(scheduleExportService.getClassScheduleGrid(loginUser.getClassId(), semester));
    }
}
