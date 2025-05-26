package com.skillup.achievements.service;

import com.skillup.achievements.model.Achievement;
import com.skillup.achievements.repository.AchievementRepository;
import com.skillup.auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementInitService {

    private final AchievementRepository achievementRepository;

    @Transactional
    public void initializeAchievements(User user) {
        // Vérifier si l'utilisateur a déjà des achievements
        if (!achievementRepository.findByUser(user).isEmpty()) {
            return;
        }

        // Liste des achievements par défaut
        // Tous les achievements commencent à 0 sauf "Premier pas" qui est automatiquement débloqué
        List<Achievement> defaultAchievements = Arrays.asList(
            // Premier pas est automatiquement débloqué lors de l'inscription
            createAchievement(user, "Premier pas", "Vous avez commencé votre parcours d'apprentissage", "trophy", 1, 1, true),
            // Les autres achievements commencent à 0 et doivent être débloqués par des actions spécifiques
            createAchievement(user, "Objectifs fixés", "Définissez votre premier objectif d'apprentissage", "target", 0, 1, false),
            createAchievement(user, "Tâches accomplies", "Complétez 5 tâches", "check", 0, 5, false),
            createAchievement(user, "Prise de notes", "Créez 10 notes", "book", 0, 10, false),
            createAchievement(user, "Apprentissage constant", "Connectez-vous 7 jours de suite", "calendar", 0, 7, false)
        );

        // Sauvegarder tous les achievements
        achievementRepository.saveAll(defaultAchievements);
    }

    private Achievement createAchievement(User user, String title, String description, String icon, int progress, int total, boolean unlocked) {
        Achievement achievement = new Achievement();
        achievement.setUser(user);
        achievement.setTitle(title);
        achievement.setDescription(description);
        achievement.setIcon(icon);
        achievement.setProgress(progress);
        achievement.setTotal(total);
        achievement.setUnlocked(unlocked);
        
        if (unlocked) {
            achievement.setUnlockedDate(LocalDateTime.now());
        }
        
        return achievement;
    }
}
