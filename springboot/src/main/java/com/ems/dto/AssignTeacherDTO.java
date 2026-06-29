package com.ems.dto;

import lombok.Data;

@Data
public class AssignTeacherDTO {
    private Long applicationId;
    private Long teacherId;
    private String roleType; // 主讲/辅讲
}
