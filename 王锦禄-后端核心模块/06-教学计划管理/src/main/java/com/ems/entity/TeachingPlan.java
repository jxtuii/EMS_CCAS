package com.ems.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("teaching_plan")
public class TeachingPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String semester;
    private Long collegeId;
    private Long courseId;
    private Long classId;
    private Integer totalHours;
    private Integer weeklyHours;
    /** 0-草稿 1-待学院配置 2-待教研室协调 3-待学院审核 4-待教务处排课 5-已发布 */
    private Integer status;
    private Long createBy;

    @TableField(exist = false)
    private String collegeName;
    @TableField(exist = false)
    private String courseName;
    @TableField(exist = false)
    private String className;
    @TableField(exist = false)
    private String realName;
}
