package com.example.planteonAiSpring.mappers;

import com.example.planteonAiSpring.dtos.NoteDTO;
import com.example.planteonAiSpring.entities.Note;

public class NoteMapper {

    public static NoteDTO toDTO(Note note) {
        return NoteDTO.builder()
                .id(note.getId())
                .content(note.getContent())
                .diaryId(note.getDiary().getId())
                .build();
    }
}
