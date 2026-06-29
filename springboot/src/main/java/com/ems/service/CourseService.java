package com.ems.service;

import com.ems.entity.Course;

import java.util.List;

public interface CourseService {
    List<Course> list();
    List<Course> listByDepartmentId(Long departmentId);
    Course getById(Long id);
    List<Course> listByCollegeId(Long collegeId);
    Course create(Course course);
    void update(Course course);
    void delete(Long id);
}
