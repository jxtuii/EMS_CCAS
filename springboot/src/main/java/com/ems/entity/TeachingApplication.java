package com.ems.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("teaching_application")
public class TeachingApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long teacherId;
    private Long teachingPlanId;
    private Integer priorityLevel;
    private String applyReason;
    /** 0-草稿 1-已提交 2-教研室通过 3-驳回 4-学院通过 */
    private Integer status;
    private LocalDateTime submitTime;

    @TableField(exist = false)
    private String teacherName;
    @TableField(exist = false)
    private String teacherNo;
    @TableField(exist = false)
    private String courseName;
    @TableField(exist = false)
    private String className;
    @TableField(exist = false)
    private String planSemester;
    @TableField(exist = false)
    private Integer totalHours;
    @TableField(exist = false)
    private Integer weeklyHours;
}
