package com.skillup.tasks.repository;

import com.skillup.auth.model.User;
import com.skillup.goals.model.Goal;
import com.skillup.tasks.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByUserOrderByDueDateAsc(User user);
    List<Task> findByUserAndStatusOrderByDueDateAsc(User user, Task.TaskStatus status);
    List<Task> findByUserAndPriorityOrderByDueDateAsc(User user, Task.TaskPriority priority);
    List<Task> findByUserAndGoalOrderByDueDateAsc(User user, Goal goal);
    List<Task> findByUserAndStatusAndPriorityOrderByDueDateAsc(
            User user, 
            Task.TaskStatus status, 
            Task.TaskPriority priority
    );
} 