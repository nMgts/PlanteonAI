package com.example.planteonAiSpring.mappers;

import com.example.planteonAiSpring.dtos.ChatMessageDTO;
import com.example.planteonAiSpring.entities.Chat;
import com.example.planteonAiSpring.entities.ChatMessage;
import com.example.planteonAiSpring.repositories.ChatRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ChatMessageMapper {
    private final ChatRepository chatRepository;

    public ChatMessageDTO toDTO(ChatMessage chatMessage) {
        return ChatMessageDTO.builder()
                .id(chatMessage.getId())
                .text(chatMessage.getText())
                .type(chatMessage.getType())
                .chatId(chatMessage.getChat().getId())
                .build();
    }

    public ChatMessage toEntity(ChatMessageDTO chatMessageDTO) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(chatMessage.getId());
        chatMessage.setText(chatMessage.getText());
        chatMessage.setType(chatMessageDTO.getType());

        Chat chat = chatRepository.findById(chatMessageDTO.getChatId()).orElseThrow();
        chatMessage.setChat(chat);
        return chatMessage;
    }
}
