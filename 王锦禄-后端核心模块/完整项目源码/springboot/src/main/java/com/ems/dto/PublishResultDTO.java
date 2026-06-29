package com.ems.dto;

import java.util.List;

/**
 * 发布教学任务结果 DTO
 */
public class PublishResultDTO {
    private int taskCount;
    private List<String> details;

    public PublishResultDTO() {}

    public PublishResultDTO(int taskCount, List<String> details) {
        this.taskCount = taskCount;
        this.details = details;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
