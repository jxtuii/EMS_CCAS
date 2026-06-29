package com.ems.dto;

import lombok.Data;

/**
 * 课表网格中的一个单元格
 */
@Data
public class GridCell {
    private String courseName;
    private String teacherName;
    /** 连续占的节次数（用于合并单元格） */
    private Integer sectionSpan;
}
