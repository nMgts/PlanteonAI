package com.example.planteonAiSpring.mappers;

import com.example.planteonAiSpring.dtos.ChatDTO;
import com.example.planteonAiSpring.entities.Chat;
import com.example.planteonAiSpring.repositories.ChatRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

public class ChatMapper {

    public static ChatDTO toDto(Chat chat) {
        return ChatDTO.builder()
                .id(chat.getId())
                .title(chat.getTitle())
                .build();
    }
}
