package com.example.planteonAiSpring.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PromptDTO {
    private long id;
    private String name;
    private String type;
    private String description;
    private String prompt;
}
