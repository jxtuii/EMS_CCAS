package com.ems.service;

import com.ems.entity.RoomPlan;

import java.util.List;

public interface RoomPlanService {
    List<RoomPlan> list();
    List<RoomPlan> listAvailable();
    RoomPlan getById(Long id);
    RoomPlan create(RoomPlan roomPlan);
    void update(RoomPlan roomPlan);
    void delete(Long id);
    void occupy(Long id, Long courseId, Long classId, Integer weekday, Integer sectionNo);
    void release(Long id);
}
