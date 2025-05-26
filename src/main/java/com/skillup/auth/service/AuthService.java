package com.skillup.auth.service;

import com.skillup.achievements.service.AchievementInitService;
import com.skillup.achievements.service.AchievementProgressService;
import com.skillup.auth.dto.AuthResponse;
import com.skillup.auth.dto.LoginRequest;
import com.skillup.auth.dto.RegisterRequest;
import com.skillup.auth.mapper.UserMapper;
import com.skillup.auth.model.User;
import com.skillup.auth.repository.UserRepository;
import com.skillup.common.security.JwtUtil;
import com.skillup.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AchievementProgressService achievementProgressService;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final ProfileService profileService;
    private final AchievementInitService achievementInitService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);
        
        // Créer automatiquement un profil avec des données par défaut
        profileService.createProfileWithDefaults(user);
        
        // Initialiser les achievements par défaut
        achievementInitService.initializeAchievements(user);

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtUtil.generateToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return new AuthResponse(
            token,
            refreshToken,
            userMapper.toDto(user)
        );
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        // Mettre à jour l'achievement "Apprentissage constant"
        achievementProgressService.checkUserLogin(user);

        String token = jwtUtil.generateToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return new AuthResponse(
            token,
            refreshToken,
            userMapper.toDto(user)
        );
    }
} 