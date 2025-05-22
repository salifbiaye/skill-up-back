package com.skillup.auth.mapper;

import com.skillup.auth.dto.AuthResponse.UserDto;
import com.skillup.auth.dto.RegisterRequest;
import com.skillup.auth.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return user;
    }

    public UserDto toDto(User user) {
        return new UserDto(
            user.getId(),
            user.getEmail(),
            user.getName()
        );
    }
} 