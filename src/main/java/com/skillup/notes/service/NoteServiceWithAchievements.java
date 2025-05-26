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

/**
 * Voici comment vous devriez modifier la classe NoteService pour intégrer les achievements.
 * Remplacez le contenu de votre classe NoteService par celui-ci.
 */
@Service
@RequiredArgsConstructor
public class NoteServiceWithAchievements {

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

    // Autres méthodes de NoteService...
}
