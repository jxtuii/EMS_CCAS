package com.ems.controller.admin;

import com.ems.common.Result;
import com.ems.common.SemesterUtils;
import com.ems.dto.PublishResultDTO;
import com.ems.dto.SchedulingResultDTO;
import com.ems.service.SchedulingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/scheduling")
@Tag(name = "管理端-排课管理")
public class SchedulingController {

    private static final Logger log = LoggerFactory.getLogger(SchedulingController.class);

    @Autowired
    private SchedulingService schedulingService;

    @PostMapping("/run")
    @Operation(summary = "执行排课（自动生成下学期课表）")
    public Result<SchedulingResultDTO> runScheduling(@RequestBody Map<String, String> params) {
        String inputSemester = params.getOrDefault("semester", "2025-2026-1");
        String outputSemester = SemesterUtils.nextSemester(inputSemester);
        log.info("触发排课: 输入学期={}, 自动推算输出学期={}", inputSemester, outputSemester);
        return Result.success(schedulingService.schedule(inputSemester, outputSemester));
    }

    @PostMapping("/publish")
    @Operation(summary = "发布教学任务书（自动生成下学期任务）")
    public Result<PublishResultDTO> publishTasks(@RequestBody Map<String, String> params) {
        String inputSemester = params.getOrDefault("semester", "2025-2026-1");
        String outputSemester = SemesterUtils.nextSemester(inputSemester);
        log.info("发布教学任务: 输入学期={}, 自动推算输出学期={}", inputSemester, outputSemester);
        return Result.success(schedulingService.publishTasks(inputSemester, outputSemester));
    }
}
