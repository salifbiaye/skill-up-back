package com.skillup.profile.repository;

import com.skillup.auth.model.User;
import com.skillup.profile.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
    Optional<UserProfile> findByUser(User user);
    boolean existsByUser(User user);
} 