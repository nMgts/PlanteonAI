package com.example.planteonAiSpring.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoteDTO {
    private long id;
    private String content;
    private long diaryId;
}
