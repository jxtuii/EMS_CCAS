package com.ems.service;

import com.ems.entity.TeachingApplication;
import com.ems.entity.TeachingPlan;

import java.util.List;

public interface TeachingApplicationService {
    List<TeachingApplication> list();
    List<TeachingApplication> listByCollegeId(Long collegeId);
    List<TeachingApplication> listByTeacherId(Long teacherId);
    TeachingApplication getById(Long id);
    TeachingApplication create(TeachingApplication app);
    List<TeachingPlan> getAvailablePlans();
}
