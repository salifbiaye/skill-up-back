package com.skillup.tasks.controller;

import com.skillup.auth.model.User;
import com.skillup.tasks.dto.TaskRequest;
import com.skillup.tasks.dto.TaskResponse;
import com.skillup.tasks.model.Task;
import com.skillup.tasks.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @RequestBody TaskRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.createTask(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable String id,
            @RequestBody TaskRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.updateTask(id, request, user));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable String id,
            @RequestParam Task.TaskStatus status,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.updateStatus(id, status, user));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getUserTasks(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Task.TaskStatus status,
            @RequestParam(required = false) Task.TaskPriority priority,
            @RequestParam(required = false) String goalId) {
        
        if (status != null && priority != null) {
            return ResponseEntity.ok(taskService.getUserTasksByStatus(user, status));
        } else if (status != null) {
            return ResponseEntity.ok(taskService.getUserTasksByStatus(user, status));
        } else if (priority != null) {
            return ResponseEntity.ok(taskService.getUserTasksByPriority(user, priority));
        } else if (goalId != null) {
            return ResponseEntity.ok(taskService.getUserTasksByGoal(user, goalId));
        }
        
        return ResponseEntity.ok(taskService.getUserTasks(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        taskService.deleteTask(id, user);
        return ResponseEntity.noContent().build();
    }
} 