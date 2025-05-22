package com.skillup.notes.controller;

import com.skillup.auth.model.User;
import com.skillup.notes.dto.NoteRequest;
import com.skillup.notes.dto.NoteResponse;
import com.skillup.notes.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(
            @RequestBody NoteRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(noteService.createNote(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(
            @PathVariable String id,
            @RequestBody NoteRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(noteService.updateNote(id, request, user));
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getUserNotes(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String goalId,
            @RequestParam(required = false) String taskId) {
        
        if (goalId != null) {
            return ResponseEntity.ok(noteService.getUserNotesByGoal(user, goalId));
        } else if (taskId != null) {
            return ResponseEntity.ok(noteService.getUserNotesByTask(user, taskId));
        }
        
        return ResponseEntity.ok(noteService.getUserNotes(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {
        noteService.deleteNote(id, user);
        return ResponseEntity.noContent().build();
    }
} 