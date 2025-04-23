package com.example.planteonAiSpring.services;

import com.example.planteonAiSpring.dtos.NoteDTO;
import com.example.planteonAiSpring.entities.Diary;
import com.example.planteonAiSpring.entities.Note;
import com.example.planteonAiSpring.mappers.NoteMapper;
import com.example.planteonAiSpring.repositories.DiaryRepository;
import com.example.planteonAiSpring.repositories.NoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final DiaryRepository diaryRepository;

    public List<NoteDTO> getNotesByDiary(Long diaryId) {
        return new ArrayList<>();
    }

    public NoteDTO createNote(NoteDTO noteDTO) {
        Note note = new Note();
        note.setContent(noteDTO.getContent());
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());

        Diary diary = diaryRepository.findById(noteDTO.getDiaryId()).orElseThrow(
                () -> new EntityNotFoundException("Diary not found")
        );
        note.setDiary(diary);

        noteRepository.save(note);
        return NoteMapper.toDTO(note);
    }
}
