package com.ems.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("teaching_accident")
public class TeachingAccident {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long teacherId;
    private String semester;
    private String level;
    private String description;
    private Integer isSerious;
    private LocalDateTime createTime;
}
