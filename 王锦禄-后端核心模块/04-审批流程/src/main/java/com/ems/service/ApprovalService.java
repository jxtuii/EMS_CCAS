package com.ems.service;

import com.ems.dto.ApprovalDTO;
import com.ems.entity.ApprovalRecord;

import java.util.List;

public interface ApprovalService {
    void approve(ApprovalDTO dto, Long approverId, String approverRole);
    List<ApprovalRecord> getRecordsByApplicationId(Long applicationId);
}
