package com.skillup.notes.repository;

import com.skillup.auth.model.User;
import com.skillup.goals.model.Goal;
import com.skillup.notes.model.Note;
import com.skillup.tasks.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, String> {
    List<Note> findByUserOrderByCreatedAtDesc(User user);
    List<Note> findByUserAndGoalOrderByCreatedAtDesc(User user, Goal goal);
    List<Note> findByUserAndTaskOrderByCreatedAtDesc(User user, Task task);
} 