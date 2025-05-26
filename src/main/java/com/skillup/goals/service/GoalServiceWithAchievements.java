package com.skillup.goals.service;

import com.skillup.achievements.service.AchievementProgressService;
import com.skillup.auth.model.User;
import com.skillup.goals.dto.GoalRequest;
import com.skillup.goals.dto.GoalResponse;
import com.skillup.goals.model.Goal;
import com.skillup.goals.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Voici comment vous devriez modifier la classe GoalService pour intégrer les achievements.
 * Remplacez le contenu de votre classe GoalService par celui-ci.
 */
@Service
@RequiredArgsConstructor
public class GoalServiceWithAchievements {

    private final GoalRepository goalRepository;
    private final AchievementProgressService achievementProgressService;

    @Transactional
    public GoalResponse createGoal(GoalRequest request, User user) {
        Goal goal = new Goal();
        goal.setTitle(request.getTitle());
        goal.setDescription(request.getDescription());
        goal.setDueDate(request.getDueDate());
        goal.setUser(user);

        goal = goalRepository.save(goal);
        
        // Mettre à jour l'achievement "Objectifs fixés"
        achievementProgressService.checkGoalCreated(user);
        
        return GoalResponse.fromEntity(goal);
    }

    // Autres méthodes de GoalService...
}
