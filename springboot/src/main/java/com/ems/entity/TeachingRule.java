package com.ems.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("teaching_rule")
public class TeachingRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("rule_name")
    private String ruleName;
    @TableField("rule_type")
    private String ruleType;
    @TableField("rule_value")
    private String ruleValue;
    private Integer enabled;
    private String description;
}
