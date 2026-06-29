package com.ems.controller.admin;

import com.ems.common.Result;
import com.ems.entity.Schedule;
import com.ems.entity.TeachingTask;
import com.ems.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@Tag(name = "管理端-报表查询")
public class ReportsController {

    @Autowired
    private RecordService recordService;

    @GetMapping("/tasks")
    @Operation(summary = "获取所有教学任务")
    public Result<List<TeachingTask>> getAllTasks() {
        return Result.success(recordService.getAllTasks());
    }

    @GetMapping("/tasks/teacher/{teacherId}")
    @Operation(summary = "获取教师教学任务")
    public Result<List<TeachingTask>> getTeacherTasks(@PathVariable Long teacherId,
                                                       @RequestParam(defaultValue = "2025-2026-1") String semester) {
        return Result.success(recordService.getTeacherTasks(teacherId, semester));
    }

    @GetMapping("/schedules")
    @Operation(summary = "获取所有课表")
    public Result<List<Schedule>> getAllSchedules(@RequestParam(defaultValue = "2025-2026-1") String semester) {
        return Result.success(recordService.getAllSchedules(semester));
    }

    @GetMapping("/semesters")
    @Operation(summary = "获取所有学期")
    public Result<List<String>> getSemesters() {
        return Result.success(recordService.getAllSemesters());
    }

    @GetMapping("/schedules/class/{classId}")
    @Operation(summary = "获取班级课表")
    public Result<List<Schedule>> getClassSchedules(@PathVariable Long classId,
                                                     @RequestParam(defaultValue = "2025-2026-1") String semester) {
        return Result.success(recordService.getClassSchedules(classId, semester));
    }
}
