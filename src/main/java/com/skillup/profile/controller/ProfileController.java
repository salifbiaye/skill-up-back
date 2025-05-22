package com.skillup.profile.controller;

import com.skillup.auth.model.User;
import com.skillup.profile.dto.ProfileRequest;
import com.skillup.profile.dto.ProfileResponse;
import com.skillup.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(profileService.createProfile(user));
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            @RequestBody ProfileRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(profileService.updateProfile(user, request));
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(profileService.getProfile(user));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProfile(
            @AuthenticationPrincipal User user) {
        profileService.deleteProfile(user);
        return ResponseEntity.noContent().build();
    }
} 