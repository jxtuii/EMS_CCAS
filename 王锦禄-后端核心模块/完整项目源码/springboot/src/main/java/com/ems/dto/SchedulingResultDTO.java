package com.ems.dto;

import lombok.Data;

@Data
public class SchedulingResultDTO {
    private int successCount;
    private int conflictCount;
    private java.util.List<String> conflicts;
}
