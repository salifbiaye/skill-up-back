package com.skillup.achievements.service;

import com.skillup.achievements.dto.AchievementResponse;
import com.skillup.achievements.model.Achievement;
import com.skillup.achievements.repository.AchievementRepository;
import com.skillup.auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final AchievementProgressService achievementProgressService;

    @Transactional(readOnly = true)
    public List<AchievementResponse> getAllAchievements(User user) {
        List<Achievement> achievements = achievementRepository.findByUser(user);
        return achievements.stream()
                .map(AchievementResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AchievementResponse getAchievement(String id, User user) {
        Achievement achievement = achievementRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Achievement not found"));
        return AchievementResponse.fromEntity(achievement);
    }
    
    /**
     * Rafraîchit tous les achievements d'un utilisateur en fonction de ses actions réelles
     * Cette méthode peut être appelée périodiquement ou manuellement pour s'assurer
     * que les achievements sont à jour
     */
    @Transactional
    public void refreshAchievements(User user) {
        achievementProgressService.refreshAllAchievements(user);
    }
}
