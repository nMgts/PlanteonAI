package com.example.planteonAiSpring.repositories;

import com.example.planteonAiSpring.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
