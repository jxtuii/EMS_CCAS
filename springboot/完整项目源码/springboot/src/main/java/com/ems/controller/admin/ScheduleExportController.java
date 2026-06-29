package com.ems.controller.admin;

import com.ems.common.Result;
import com.ems.dto.ClassScheduleGrid;
import com.ems.dto.TeacherScheduleDTO;
import com.ems.service.ScheduleExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reports")
@Tag(name = "管理端-排课成果导出")
public class ScheduleExportController {

    @Autowired
    private ScheduleExportService scheduleExportService;

    @GetMapping("/teacher-schedule/{teacherId}")
    @Operation(summary = "教师教学任务书（合并连续节次）")
    public Result<TeacherScheduleDTO> getTeacherSchedule(
            @PathVariable Long teacherId,
            @RequestParam(defaultValue = "2025-2026-2") String semester) {
        return Result.success(scheduleExportService.getTeacherSchedule(teacherId, semester));
    }

    @GetMapping("/class-grid/{classId}")
    @Operation(summary = "班级课表（网格格式）")
    public Result<ClassScheduleGrid> getClassGrid(
            @PathVariable Long classId,
            @RequestParam(defaultValue = "2025-2026-2") String semester) {
        return Result.success(scheduleExportService.getClassScheduleGrid(classId, semester));
    }
}
