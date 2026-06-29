package com.ems.service;

import com.ems.entity.TaskPool;

import java.util.List;

public interface TaskPoolService {
    List<TaskPool> list();
    List<TaskPool> listPending();
    TaskPool getById(Long id);
    TaskPool create(TaskPool taskPool);
    void update(TaskPool taskPool);
    void delete(Long id);
    void assignTeacher(Long id, Long teacherId);
}
