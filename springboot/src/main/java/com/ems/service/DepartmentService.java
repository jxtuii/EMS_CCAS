package com.ems.service;

import com.ems.entity.Department;

import java.util.List;

public interface DepartmentService {
    List<Department> list();
    List<Department> listByCollegeId(Long collegeId);
}
