package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.entity.TimeSlot;

import java.util.List;

public interface TimeSlotMapper extends BaseMapper<TimeSlot> {
    List<TimeSlot> selectByWeekday(Integer weekday);
}
