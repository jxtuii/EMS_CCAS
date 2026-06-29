package com.ems.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("schedule")
public class Schedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String semester;
    private Long classId;
    private Long courseId;
    private Long teacherId;
    private Long timeSlotId;

    @TableField(exist = false)
    private String className;
    @TableField(exist = false)
    private String courseName;
    @TableField(exist = false)
    private String teacherName;
    @TableField(exist = false)
    private Integer weekday;
    @TableField(exist = false)
    private Integer sectionNo;
    @TableField(exist = false)
    private String startTime;
    @TableField(exist = false)
    private String endTime;
}
