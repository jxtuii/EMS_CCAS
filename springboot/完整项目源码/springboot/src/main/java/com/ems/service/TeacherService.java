package com.ems.service;

import com.ems.entity.Teacher;

import java.util.List;

public interface TeacherService {
    List<Teacher> list();
    List<Teacher> listByCollegeId(Long collegeId);
    Teacher getById(Long id);
    Teacher getByUserId(Long userId);
}
