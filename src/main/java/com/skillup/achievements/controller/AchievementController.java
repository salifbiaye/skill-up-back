package com.skillup.achievements.controller;

import com.skillup.achievements.dto.AchievementResponse;
import com.skillup.achievements.service.AchievementService;
import com.skillup.auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    @GetMapping
    public ResponseEntity<List<AchievementResponse>> getAllAchievements(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(achievementService.getAllAchievements(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AchievementResponse> getAchievement(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(achievementService.getAchievement(id, user));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<List<AchievementResponse>> refreshAchievements(
            @AuthenticationPrincipal User user) {
        // Rafraîchir les achievements en fonction des actions réelles de l'utilisateur
        achievementService.refreshAchievements(user);
        // Retourner la liste mise à jour des achievements
        return ResponseEntity.ok(achievementService.getAllAchievements(user));
    }
}
