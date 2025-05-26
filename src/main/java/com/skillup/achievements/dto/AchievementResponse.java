package com.skillup.achievements.dto;

import com.skillup.achievements.model.Achievement;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AchievementResponse {
    private String id;
    private String title;
    private String description;
    private String icon;
    private boolean unlocked;
    private LocalDateTime date;
    private int progress;
    private int total;

    public static AchievementResponse fromEntity(Achievement achievement) {
        AchievementResponse response = new AchievementResponse();
        response.setId(achievement.getId());
        response.setTitle(achievement.getTitle());
        response.setDescription(achievement.getDescription());
        response.setIcon(achievement.getIcon());
        response.setUnlocked(achievement.isUnlocked());
        response.setDate(achievement.getUnlockedDate());
        response.setProgress(achievement.getProgress());
        response.setTotal(achievement.getTotal());
        return response;
    }
}
