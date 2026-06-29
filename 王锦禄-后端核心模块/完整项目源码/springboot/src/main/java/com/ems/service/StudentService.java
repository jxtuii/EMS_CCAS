package com.ems.service;

import com.ems.entity.Student;

import java.util.List;

public interface StudentService {
    List<Student> list();
    Student getById(Long id);
    Student getByUserId(Long userId);
    Student getByStudentNo(String studentNo);
    Student create(Student student);
    void update(Student student);
    void delete(Long id);
}
