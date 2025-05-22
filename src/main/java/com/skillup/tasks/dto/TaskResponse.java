package com.skillup.tasks.dto;

import com.skillup.tasks.model.Task;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private String id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Task.TaskStatus status;
    private Task.TaskPriority priority;
    private String goalId;
    private String goalTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskResponse fromEntity(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setDueDate(task.getDueDate());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        
        if (task.getGoal() != null) {
            response.setGoalId(task.getGoal().getId());
            response.setGoalTitle(task.getGoal().getTitle());
        }
        
        return response;
    }
} 