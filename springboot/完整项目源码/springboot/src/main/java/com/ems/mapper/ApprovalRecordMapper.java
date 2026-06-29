package com.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ems.entity.ApprovalRecord;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ApprovalRecordMapper extends BaseMapper<ApprovalRecord> {

    @Select("SELECT ar.*, u.real_name as approver_name " +
            "FROM approval_record ar " +
            "LEFT JOIN sys_user u ON ar.approver_id = u.id " +
            "WHERE ar.application_id = #{applicationId} " +
            "ORDER BY ar.create_time DESC")
    List<ApprovalRecord> selectByApplicationId(Long applicationId);
}
