package com.skillup.profile.service;

import com.skillup.auth.model.User;
import com.skillup.profile.dto.ProfileRequest;
import com.skillup.profile.dto.ProfileResponse;
import com.skillup.profile.model.UserProfile;
import com.skillup.profile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserProfileRepository profileRepository;

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
} 