package com.skillup.profile.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileRequest {
    @Size(max = 100, message = "Full name must be less than 100 characters")
    private String fullName;

    @Size(max = 500, message = "Bio must be less than 500 characters")
    private String bio;

    @Size(max = 100, message = "Location must be less than 100 characters")
    private String location;

    @Size(max = 100, message = "Occupation must be less than 100 characters")
    private String occupation;

    @Size(max = 200, message = "Avatar URL must be less than 200 characters")
    private String avatarUrl;
} 