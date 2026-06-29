package com.ems.service;

import com.ems.entity.Clazz;

import java.util.List;

public interface ClazzService {
    List<Clazz> list();
    List<Clazz> listByCollegeId(Long collegeId);
}
