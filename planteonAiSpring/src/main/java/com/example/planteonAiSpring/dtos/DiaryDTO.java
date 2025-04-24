package com.example.planteonAiSpring.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiaryDTO {
    private long id;
    private String title;
}
