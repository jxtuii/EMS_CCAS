package com.ems.dto;

import lombok.Data;

@Data
public class ApprovalDTO {
    private Long applicationId;
    private Boolean approved;
    private String comment;
}
