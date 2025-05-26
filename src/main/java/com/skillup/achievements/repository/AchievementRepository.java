package com.skillup.achievements.repository;

import com.skillup.achievements.model.Achievement;
import com.skillup.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, String> {
    List<Achievement> findByUser(User user);
    Optional<Achievement> findByIdAndUser(String id, User user);
    Optional<Achievement> findByUserAndTitle(User user, String title);
}
