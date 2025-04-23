package com.example.planteonAiSpring.tools;

import com.example.planteonAiSpring.dtos.NoteDTO;
import com.example.planteonAiSpring.entities.Diary;
import com.example.planteonAiSpring.entities.Note;
import com.example.planteonAiSpring.repositories.DiaryRepository;
import com.example.planteonAiSpring.services.NoteService;
import dev.langchain4j.agent.tool.Tool;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NotesTools {
    private final NoteService noteService;
    private final DiaryRepository diaryRepository;

    @Tool
    public NoteDTO createNote(String content, String diaryTitle) {
        Diary diary = diaryRepository.findByTitle(diaryTitle).orElseThrow(
                () -> new EntityNotFoundException("Diary not found")
        );

        System.out.println("content: " + content);
        System.out.println("diary: " + diaryTitle);

        NoteDTO noteDTO = NoteDTO.builder()
                .content(content)
                .diaryId(diary.getId())
                .build();
        return noteService.createNote(noteDTO);
    }
}
