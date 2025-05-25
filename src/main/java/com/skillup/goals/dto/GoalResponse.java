package com.skillup.goals.dto;

import com.skillup.goals.model.Goal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponse {
    private String id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String status;
    private Integer progress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userId;
    private String userName;

    public static GoalResponse fromEntity(Goal goal) {
        return GoalResponse.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .dueDate(goal.getDueDate())
                .status(goal.getStatus().name())
                .progress(goal.getProgress())
                .createdAt(goal.getCreatedAt())
                .updatedAt(goal.getUpdatedAt())
                .userId(goal.getUser().getId())
                .userName(goal.getUser().getName())
                .build();
    }
} 