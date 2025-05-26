package com.skillup.goals.service;

import com.skillup.achievements.service.AchievementProgressService;
import com.skillup.auth.model.User;
import com.skillup.goals.controller.GoalController;
import com.skillup.goals.dto.GoalRequest;
import com.skillup.goals.dto.GoalResponse;
import com.skillup.goals.model.Goal;
import com.skillup.goals.repository.GoalRepository;
import com.skillup.tasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final TaskRepository taskRepository;
    private final AchievementProgressService achievementProgressService;
    private static final Logger logger = LoggerFactory.getLogger(GoalController.class);

    @Transactional
    public GoalResponse createGoal(GoalRequest request, User user) {
        Goal goal = new Goal();
        goal.setTitle(request.getTitle());
        goal.setDescription(request.getDescription());
        goal.setDueDate(request.getDueDate());
        goal.setUser(user);

        goal = goalRepository.save(goal);

        achievementProgressService.checkGoalCreated(user);

        return GoalResponse.fromEntity(goal);
    }

    @Transactional
    public GoalResponse updateGoal(String id, GoalRequest request, User user) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this goal");
        }

        goal.setTitle(request.getTitle());
        goal.setDescription(request.getDescription());
        goal.setDueDate(request.getDueDate());

        goal = goalRepository.save(goal);
        return GoalResponse.fromEntity(goal);
    }

    @Transactional
    public GoalResponse updateProgress(String id, Integer progress, User user) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this goal");
        }

        goal.setProgress(progress);
        if (progress >= 100) {
            goal.setStatus(Goal.GoalStatus.COMPLETED);
        } else if (progress > 0) {
            goal.setStatus(Goal.GoalStatus.IN_PROGRESS);
        }

        goal = goalRepository.save(goal);
        return GoalResponse.fromEntity(goal);
    }

    @Transactional
    public GoalResponse updateStatus(String id, Goal.GoalStatus status, User user) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this goal");
        }

        goal.setStatus(status);
        if (status == Goal.GoalStatus.COMPLETED) {
            goal.setProgress(100);
        }

        goal = goalRepository.save(goal);
        return GoalResponse.fromEntity(goal);
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> getUserGoals(User user) {
        return goalRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(GoalResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> getUserGoalsByStatus(User user, Goal.GoalStatus status) {
        return goalRepository.findByUserAndStatusOrderByCreatedAtDesc(user, status)
                .stream()
                .map(GoalResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteGoal(String id, User user) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to delete this goal");
        }
        
        // Vérifier si l'objectif a des tâches associées
        if (taskRepository.existsByGoal(goal)) {
            // L'objectif a des tâches associées, ne pas le supprimer
            logger.info("Cannot delete goal {} because it has associated tasks", id);
            return false;
        }

        // L'objectif n'a pas de tâches associées, on peut le supprimer
        goalRepository.delete(goal);
        return true;
    }
}
