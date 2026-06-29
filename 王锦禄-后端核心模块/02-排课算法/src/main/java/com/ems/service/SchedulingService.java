package com.ems.service;

import com.ems.dto.PublishResultDTO;
import com.ems.dto.SchedulingResultDTO;

public interface SchedulingService {
    /**
     * 执行排课：使用 inputSemester 筛选待排课的教学计划，
     * 将生成的课表写入 outputSemester。
     *
     * @param inputSemester  输入学期（用于筛选教学计划）
     * @param outputSemester 输出学期（生成课表的所属学期）
     */
    SchedulingResultDTO schedule(String inputSemester, String outputSemester);

    /**
     * 发布教学任务：使用 inputSemester 筛选已排课的教学计划，
     * 将生成的教学任务写入 outputSemester。
     *
     * @param inputSemester  输入学期（用于筛选已排课计划）
     * @param outputSemester 输出学期（生成教学任务的所属学期）
     */
    PublishResultDTO publishTasks(String inputSemester, String outputSemester);
}
