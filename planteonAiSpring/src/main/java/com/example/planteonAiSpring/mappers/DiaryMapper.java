package com.example.planteonAiSpring.mappers;

import com.example.planteonAiSpring.dtos.DiaryDTO;
import com.example.planteonAiSpring.entities.Diary;

public class DiaryMapper {

    public static DiaryDTO toDTO(Diary diary) {
        return DiaryDTO.builder()
                .id(diary.getId())
                .title(diary.getTitle())
                .build();
    }
}
