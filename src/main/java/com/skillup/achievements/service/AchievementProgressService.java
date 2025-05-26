package com.skillup.achievements.service;

import com.skillup.achievements.model.Achievement;
import com.skillup.achievements.repository.AchievementRepository;
import com.skillup.auth.model.User;
import com.skillup.goals.repository.GoalRepository;
import com.skillup.notes.repository.NoteRepository;
import com.skillup.tasks.model.Task;
import com.skillup.tasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AchievementProgressService {

    private final AchievementRepository achievementRepository;
    private final GoalRepository goalRepository;
    private final TaskRepository taskRepository;
    private final NoteRepository noteRepository;

    /**
     * Incrémente le progrès d'un achievement spécifique et le débloque si le progrès atteint le total
     */
    @Transactional
    public void incrementAchievementProgress(User user, String achievementTitle, int incrementValue) {
        Optional<Achievement> achievementOpt = achievementRepository.findByUserAndTitle(user, achievementTitle);
        
        if (achievementOpt.isPresent()) {
            Achievement achievement = achievementOpt.get();
            
            if (!achievement.isUnlocked()) {
                int newProgress = achievement.getProgress() + incrementValue;
                achievement.setProgress(Math.min(newProgress, achievement.getTotal()));
                
                // Débloquer l'achievement si le progrès atteint le total
                if (achievement.getProgress() >= achievement.getTotal()) {
                    achievement.setUnlocked(true);
                    achievement.setUnlockedDate(LocalDateTime.now());
                }
                
                achievementRepository.save(achievement);
            }
        }
    }
    
    /**
     * Vérifie et met à jour l'achievement "Objectifs fixés" quand un utilisateur crée un objectif
     */
    @Transactional
    public void checkGoalCreated(User user) {
        incrementAchievementProgress(user, "Objectifs fixés", 1);
    }
    
    /**
     * Vérifie et met à jour l'achievement "Tâches accomplies" quand un utilisateur complète une tâche
     */
    @Transactional
    public void checkTaskCompleted(User user) {
        incrementAchievementProgress(user, "Tâches accomplies", 1);
    }
    
    /**
     * Vérifie et met à jour l'achievement "Prise de notes" quand un utilisateur crée une note
     */
    @Transactional
    public void checkNoteCreated(User user) {
        incrementAchievementProgress(user, "Prise de notes", 1);
    }
    
    /**
     * Vérifie et met à jour l'achievement "Apprentissage constant" quand un utilisateur se connecte
     */
    @Transactional
    public void checkUserLogin(User user) {
        // Pour une implémentation plus réaliste, nous devrions stocker la date de la dernière connexion
        // et vérifier si la connexion actuelle est consécutive (le jour suivant)
        // Pour l'instant, on incrémente simplement le compteur
        incrementAchievementProgress(user, "Apprentissage constant", 1);
    }
    
    /**
     * Vérifie tous les achievements d'un utilisateur et les met à jour en fonction de ses actions réelles
     */
    @Transactional
    public void refreshAllAchievements(User user) {
        // Récupérer tous les achievements de l'utilisateur
        List<Achievement> achievements = achievementRepository.findByUser(user);
        
        for (Achievement achievement : achievements) {
            // Ne pas traiter les achievements déjà débloqués
            if (achievement.isUnlocked()) {
                continue;
            }
            
            // Mettre à jour chaque achievement en fonction de son titre
            switch (achievement.getTitle()) {
                case "Objectifs fixés":
                    // Compter le nombre d'objectifs créés par l'utilisateur
                    long goalCount = goalRepository.countByUser(user);
                    achievement.setProgress((int) Math.min(goalCount, achievement.getTotal()));
                    break;
                    
                case "Tâches accomplies":
                    // Compter le nombre de tâches complétées par l'utilisateur
                    long completedTaskCount = taskRepository.countByUserAndStatus(user, Task.TaskStatus.COMPLETED);
                    achievement.setProgress((int) Math.min(completedTaskCount, achievement.getTotal()));
                    break;
                    
                case "Prise de notes":
                    // Compter le nombre de notes créées par l'utilisateur
                    long noteCount = noteRepository.countByUser(user);
                    achievement.setProgress((int) Math.min(noteCount, achievement.getTotal()));
                    break;
                    
                // Pour "Apprentissage constant", nous ne pouvons pas facilement recalculer sans historique de connexion
                // Pour "Premier pas", il est déjà débloqué lors de l'inscription
            }
            
            // Débloquer l'achievement si le progrès atteint le total
            if (achievement.getProgress() >= achievement.getTotal()) {
                achievement.setUnlocked(true);
                achievement.setUnlockedDate(LocalDateTime.now());
            }
            
            // Sauvegarder les changements
            achievementRepository.save(achievement);
        }
    }
    
    /**
     * Vérifie si un achievement spécifique est débloqué
     */
    @Transactional(readOnly = true)
    public boolean isAchievementUnlocked(User user, String achievementTitle) {
        Optional<Achievement> achievementOpt = achievementRepository.findByUserAndTitle(user, achievementTitle);
        return achievementOpt.isPresent() && achievementOpt.get().isUnlocked();
    }
    
    /**
     * Récupère le progrès d'un achievement spécifique
     */
    @Transactional(readOnly = true)
    public int getAchievementProgress(User user, String achievementTitle) {
        Optional<Achievement> achievementOpt = achievementRepository.findByUserAndTitle(user, achievementTitle);
        return achievementOpt.map(Achievement::getProgress).orElse(0);
    }
}
