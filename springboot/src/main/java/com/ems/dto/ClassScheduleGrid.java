package com.ems.dto;

import lombok.Data;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 班级课表（网格格式）
 */
@Data
public class ClassScheduleGrid {
    private String className;
    private String semester;
    /** 星期顺序 */
    private List<String> weekLabels;
    /** 节次顺序 */
    private List<Integer> sections;
    /** weekday(1-5) → sectionNo(1-5) → cell */
    private Map<Integer, Map<Integer, GridCell>> grid;

    public ClassScheduleGrid() {
        this.grid = new LinkedHashMap<>();
    }
}
