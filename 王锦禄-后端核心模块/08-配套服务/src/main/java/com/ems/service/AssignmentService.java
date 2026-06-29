package com.ems.service;

import com.ems.dto.AssignTeacherDTO;
import com.ems.entity.TeachingTask;

import java.util.List;

public interface AssignmentService {
    List<String> validateRules(AssignTeacherDTO dto);
    TeachingTask assign(AssignTeacherDTO dto);
}
