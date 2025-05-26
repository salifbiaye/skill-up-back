package com.skillup.notes.service;

import com.skillup.achievements.service.AchievementProgressService;
import com.skillup.auth.model.User;
import com.skillup.goals.model.Goal;
import com.skillup.goals.repository.GoalRepository;
import com.skillup.notes.dto.NoteRequest;
import com.skillup.notes.dto.NoteResponse;
import com.skillup.notes.model.Note;
import com.skillup.notes.repository.NoteRepository;
import com.skillup.tasks.model.Task;
import com.skillup.tasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final GoalRepository goalRepository;
    private final TaskRepository taskRepository;
    private final AchievementProgressService achievementProgressService;

    @Transactional
    public NoteResponse createNote(NoteRequest request, User user) {
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setUser(user);

        if (request.getGoalId() != null) {
            Goal goal = goalRepository.findById(request.getGoalId())
                    .orElseThrow(() -> new RuntimeException("Goal not found"));
            if (!goal.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Not authorized to create note for this goal");
            }
            note.setGoal(goal);
        }

        if (request.getTaskId() != null) {
            Task task = taskRepository.findById(request.getTaskId())
                    .orElseThrow(() -> new RuntimeException("Task not found"));
            if (!task.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Not authorized to create note for this task");
            }
            note.setTask(task);
        }

        note = noteRepository.save(note);

        // Mettre à jour l'achievement "Prise de notes"
        achievementProgressService.checkNoteCreated(user);

        return NoteResponse.fromEntity(note);
    }

    @Transactional
    public NoteResponse updateNote(String id, NoteRequest request, User user) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this note");
        }

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());

        if (request.getGoalId() != null) {
            Goal goal = goalRepository.findById(request.getGoalId())
                    .orElseThrow(() -> new RuntimeException("Goal not found"));
            if (!goal.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Not authorized to assign note to this goal");
            }
            note.setGoal(goal);
        } else {
            note.setGoal(null);
        }

        if (request.getTaskId() != null) {
            Task task = taskRepository.findById(request.getTaskId())
                    .orElseThrow(() -> new RuntimeException("Task not found"));
            if (!task.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Not authorized to assign note to this task");
            }
            note.setTask(task);
        } else {
            note.setTask(null);
        }

        note = noteRepository.save(note);
        return NoteResponse.fromEntity(note);
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getUserNotes(User user) {
        return noteRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(NoteResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getUserNotesByGoal(User user, String goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to view notes for this goal");
        }

        return noteRepository.findByUserAndGoalOrderByCreatedAtDesc(user, goal)
                .stream()
                .map(NoteResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getUserNotesByTask(User user, String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to view notes for this task");
        }

        return noteRepository.findByUserAndTaskOrderByCreatedAtDesc(user, task)
                .stream()
                .map(NoteResponse::fromEntity)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public NoteResponse getNoteById(String id, User user) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to view this note");
        }

        return NoteResponse.fromEntity(note);
    }

    @Transactional
    public void deleteNote(String id, User user) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to delete this note");
        }

        noteRepository.delete(note);
    }
}
