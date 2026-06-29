package com.ems.service;

import com.ems.entity.ExamPlan;

import java.util.List;

public interface ExamPlanService {
    List<ExamPlan> list();
    List<ExamPlan> listByClassId(Long classId);
    ExamPlan getById(Long id);
    ExamPlan create(ExamPlan examPlan);
    void update(ExamPlan examPlan);
    void delete(Long id);
    void publish(Long id);
}
