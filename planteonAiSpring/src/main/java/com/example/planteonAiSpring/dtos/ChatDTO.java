package com.example.planteonAiSpring.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ChatDTO {
    private UUID id;
    private String title;
}
