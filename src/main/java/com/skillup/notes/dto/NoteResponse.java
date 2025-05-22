package com.skillup.notes.dto;

import com.skillup.notes.model.Note;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoteResponse {
    private String id;
    private String title;
    private String content;
    private String goalId;
    private String goalTitle;
    private String taskId;
    private String taskTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NoteResponse fromEntity(Note note) {
        NoteResponse response = new NoteResponse();
        response.setId(note.getId());
        response.setTitle(note.getTitle());
        response.setContent(note.getContent());
        response.setCreatedAt(note.getCreatedAt());
        response.setUpdatedAt(note.getUpdatedAt());
        
        if (note.getGoal() != null) {
            response.setGoalId(note.getGoal().getId());
            response.setGoalTitle(note.getGoal().getTitle());
        }
        
        if (note.getTask() != null) {
            response.setTaskId(note.getTask().getId());
            response.setTaskTitle(note.getTask().getTitle());
        }
        
        return response;
    }
} 