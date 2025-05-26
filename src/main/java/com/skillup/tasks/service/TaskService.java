package com.skillup.tasks.service;

import com.skillup.achievements.service.AchievementProgressService;
import com.skillup.auth.model.User;
import com.skillup.goals.model.Goal;
import com.skillup.goals.repository.GoalRepository;
import com.skillup.tasks.dto.TaskRequest;
import com.skillup.tasks.dto.TaskResponse;
import com.skillup.tasks.model.Task;
import com.skillup.tasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;
    private final AchievementProgressService achievementProgressService;

    @Transactional
    public TaskResponse createTask(TaskRequest request, User user) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setUser(user);

        if (request.getGoalId() != null) {
            Goal goal = goalRepository.findById(request.getGoalId())
                    .orElseThrow(() -> new RuntimeException("Goal not found"));
            if (!goal.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Not authorized to assign task to this goal");
            }
            task.setGoal(goal);
        }

        task = taskRepository.save(task);
        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public TaskResponse updateTask(String id, TaskRequest request, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this task");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());

        if (request.getGoalId() != null) {
            Goal goal = goalRepository.findById(request.getGoalId())
                    .orElseThrow(() -> new RuntimeException("Goal not found"));
            if (!goal.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Not authorized to assign task to this goal");
            }
            task.setGoal(goal);
        } else {
            task.setGoal(null);
        }

        task = taskRepository.save(task);
        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public TaskResponse updateStatus(String id, Task.TaskStatus status, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this task");
        }

        task.setStatus(status);
        task = taskRepository.save(task);
        
        // Si la tâche est marquée comme terminée, mettre à jour l'achievement "Tâches accomplies"
        if (status == Task.TaskStatus.COMPLETED) {
            achievementProgressService.checkTaskCompleted(user);
        }
        
        return TaskResponse.fromEntity(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getUserTasks(User user) {
        return taskRepository.findByUserOrderByDueDateAsc(user)
                .stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getUserTasksByStatus(User user, Task.TaskStatus status) {
        return taskRepository.findByUserAndStatusOrderByDueDateAsc(user, status)
                .stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getUserTasksByPriority(User user, Task.TaskPriority priority) {
        return taskRepository.findByUserAndPriorityOrderByDueDateAsc(user, priority)
                .stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getUserTasksByGoal(User user, String goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to view tasks for this goal");
        }

        return taskRepository.findByUserAndGoalOrderByDueDateAsc(user, goal)
                .stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTask(String id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to delete this task");
        }

        taskRepository.delete(task);
    }
} 