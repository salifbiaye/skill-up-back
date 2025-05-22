package com.skillup.profile.dto;

import com.skillup.profile.model.UserProfile;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProfileResponse {
    private String id;
    private String userId;
    private String email;
    private String fullName;
    private String bio;
    private String location;
    private String occupation;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProfileResponse fromEntity(UserProfile profile) {
        ProfileResponse response = new ProfileResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUser().getId());
        response.setEmail(profile.getUser().getEmail());
        response.setFullName(profile.getFullName());
        response.setBio(profile.getBio());
        response.setLocation(profile.getLocation());
        response.setOccupation(profile.getOccupation());
        response.setAvatarUrl(profile.getAvatarUrl());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());
        return response;
    }
} 