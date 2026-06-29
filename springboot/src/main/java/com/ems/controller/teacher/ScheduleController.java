package com.ems.controller.teacher;

import com.ems.common.Result;
import com.ems.dto.TeacherScheduleDTO;
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

@RestController("teacherScheduleController")
@RequestMapping("/api/teacher/schedules")
@Tag(name = "教师端-我的课表")
public class ScheduleController {

    @Autowired
    private RecordService recordService;
    @Autowired
    private ScheduleExportService scheduleExportService;

    @GetMapping
    @Operation(summary = "获取我的课表（原始数据）")
    public Result<List<Schedule>> mySchedules(@AuthenticationPrincipal LoginUser loginUser,
                                               @RequestParam(defaultValue = "2025-2026-1") String semester) {
        if (loginUser.getTeacherId() == null) {
            return Result.error("当前用户不是教师");
        }
        return Result.success(recordService.getTeacherSchedules(loginUser.getTeacherId(), semester));
    }

    @GetMapping("/formatted")
    @Operation(summary = "获取教学任务书（合并连续节次）")
    public Result<TeacherScheduleDTO> myFormattedSchedule(@AuthenticationPrincipal LoginUser loginUser,
                                                           @RequestParam(defaultValue = "2025-2026-2") String semester) {
        if (loginUser.getTeacherId() == null) {
            return Result.error("当前用户不是教师");
        }
        return Result.success(scheduleExportService.getTeacherSchedule(loginUser.getTeacherId(), semester));
    }
}
