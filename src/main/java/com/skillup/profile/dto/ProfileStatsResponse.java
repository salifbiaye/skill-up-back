package com.skillup.profile.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProfileStatsResponse {
    private int totalObjectives;
    private int completedObjectives;
    private int inProgressObjectives;
    private int totalTasks;
    private int completedTasks;
    private int inProgressTasks;
    private int overdueTasks;
    private int totalNotes;
    private int notesWithAiSummary;
    private int joinedDays;
    private LocalDateTime lastUpdated;
}
