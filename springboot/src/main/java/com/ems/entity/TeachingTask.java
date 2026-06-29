package com.ems.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("teaching_task")
public class TeachingTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long teacherId;
    private Long courseId;
    private Long classId;
    private String semester;
    private String roleType;
    private Integer totalHours;
    private Integer weeklyHours;
    private Integer status;

    @TableField(exist = false)
    private String teacherName;
    @TableField(exist = false)
    private String teacherNo;
    @TableField(exist = false)
    private String courseName;
    @TableField(exist = false)
    private String className;
}
