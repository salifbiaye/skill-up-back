package com.skillup.goals.repository;

import com.skillup.auth.model.User;
import com.skillup.goals.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, String> {
    List<Goal> findByUserOrderByCreatedAtDesc(User user);
    List<Goal> findByUserAndStatusOrderByCreatedAtDesc(User user, Goal.GoalStatus status);
    long countByUser(User user);
}