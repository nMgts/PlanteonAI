package com.example.planteonAiSpring.dtos;

import com.example.planteonAiSpring.types.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private UUID id;
    private String text;
    private LocalDateTime createdAt;
    private MessageType type;
    private UUID chatId;
}
