package com.skillup.goals.controller;

import com.skillup.auth.model.User;
import com.skillup.goals.dto.GoalRequest;
import com.skillup.goals.dto.GoalResponse;
import com.skillup.goals.model.Goal;
import com.skillup.goals.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @RequestBody GoalRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.createGoal(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(
            @PathVariable String id,
            @RequestBody GoalRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.updateGoal(id, request, user));
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<GoalResponse> updateProgress(
            @PathVariable String id,
            @RequestParam Integer progress,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.updateProgress(id, progress, user));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<GoalResponse> updateStatus(
            @PathVariable String id,
            @RequestParam Goal.GoalStatus status,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.updateStatus(id, status, user));
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> getUserGoals(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Goal.GoalStatus status) {
        if (status != null) {
            return ResponseEntity.ok(goalService.getUserGoalsByStatus(user, status));
        }
        return ResponseEntity.ok(goalService.getUserGoals(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoalById(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.getGoalById(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        boolean deleted = goalService.deleteGoal(id, user);
        
        if (deleted) {
            // L'objectif a été supprimé avec succès
            return ResponseEntity.noContent().build();
        } else {
            // L'objectif n'a pas pu être supprimé car il a des tâches associées
            Map<String, String> response = new HashMap<>();
            response.put("message", "Impossible de supprimer cet objectif car il a des tâches associées.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}