package com.ems.service;

import com.ems.entity.College;

import java.util.List;

public interface CollegeService {
    List<College> list();
    College getById(Long id);
}
