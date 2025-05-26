package com.skillup.profile.service;

import com.skillup.auth.model.User;
import com.skillup.auth.repository.UserRepository;
import com.skillup.profile.dto.PasswordChangeRequest;
import com.skillup.profile.dto.ProfileRequest;
import com.skillup.profile.dto.ProfileResponse;
import com.skillup.profile.dto.ProfileStatsResponse;
import com.skillup.profile.model.UserProfile;
import com.skillup.profile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ProfileResponse createProfile(User user) {
        if (profileRepository.existsByUser(user)) {
            throw new RuntimeException("Profile already exists for this user");
        }

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile = profileRepository.save(profile);
        return ProfileResponse.fromEntity(profile);
    }
    
    @Transactional
    public ProfileResponse createProfileWithDefaults(User user) {
        if (profileRepository.existsByUser(user)) {
            return getProfile(user);
        }

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        
        // Définir des valeurs par défaut
        profile.setFullName(user.getName());
        profile.setBio("Nouvel utilisateur de SkillUp");
        profile.setLocation("Non spécifié");
        profile.setOccupation("Non spécifié");
        profile.setAvatarUrl("/assets/images/default-avatar.png");
        
        profile = profileRepository.save(profile);
        return ProfileResponse.fromEntity(profile);
    }

    @Transactional
    public ProfileResponse updateProfile(User user, ProfileRequest request) {
        UserProfile profile = profileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        if (request.getFullName() != null) {
            profile.setFullName(request.getFullName());
        }
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getLocation() != null) {
            profile.setLocation(request.getLocation());
        }
        if (request.getOccupation() != null) {
            profile.setOccupation(request.getOccupation());
        }
        if (request.getAvatarUrl() != null) {
            profile.setAvatarUrl(request.getAvatarUrl());
        }

        profile = profileRepository.save(profile);
        return ProfileResponse.fromEntity(profile);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(User user) {
        UserProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return ProfileResponse.fromEntity(profile);
    }

    @Transactional
    public void deleteProfile(User user) {
        UserProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        profileRepository.delete(profile);
    }

    @Transactional
    public boolean changePassword(User user, PasswordChangeRequest request) {
        // Vérifier que le mot de passe actuel est correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return false;
        }
        
        // Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        
        // Sauvegarder l'utilisateur avec le nouveau mot de passe
        userRepository.save(user);
        
        return true;
    }

    @Transactional(readOnly = true)
    public ProfileStatsResponse getProfileStats(User user) {
        UserProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        ProfileStatsResponse stats = new ProfileStatsResponse();
        
        // Ces valeurs devraient être calculées à partir des données réelles
        // Ceci est juste un exemple
        stats.setTotalObjectives(10);
        stats.setCompletedObjectives(5);
        stats.setInProgressObjectives(5);
        stats.setTotalTasks(20);
        stats.setCompletedTasks(15);
        stats.setInProgressTasks(3);
        stats.setOverdueTasks(2);
        stats.setTotalNotes(30);
        stats.setNotesWithAiSummary(10);
        
        // Calculer le nombre de jours depuis l'inscription
        stats.setJoinedDays((int) ChronoUnit.DAYS.between(profile.getCreatedAt(), LocalDateTime.now()));
        stats.setLastUpdated(LocalDateTime.now());
        
        return stats;
    }

    @Transactional
    public String uploadAvatar(User user, MultipartFile avatar) throws IOException {
        UserProfile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        // Créer le répertoire pour stocker les avatars s'il n'existe pas
        String uploadDir = "uploads/avatars/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Générer un nom de fichier unique
        String fileName = user.getId() + "_" + System.currentTimeMillis() + "_" + avatar.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        
        // Enregistrer le fichier
        Files.copy(avatar.getInputStream(), filePath);
        
        // Mettre à jour l'URL de l'avatar dans le profil
        String avatarUrl = "/" + uploadDir + fileName;
        profile.setAvatarUrl(avatarUrl);
        profileRepository.save(profile);
        
        return avatarUrl;
    }
}