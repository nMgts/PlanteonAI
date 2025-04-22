package com.example.planteonAiSpring.services;

import com.example.planteonAiSpring.controllers.N8nController;
import com.example.planteonAiSpring.dtos.ChatMessageDTO;
import com.example.planteonAiSpring.entities.Chat;
import com.example.planteonAiSpring.entities.ChatMessage;
import com.example.planteonAiSpring.mappers.ChatMessageMapper;
import com.example.planteonAiSpring.repositories.ChatMessageRepository;
import com.example.planteonAiSpring.repositories.ChatRepository;
import com.example.planteonAiSpring.types.MessageType;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRepository chatRepository;
    private final N8nController n8nController;
    private final ChatMessageMapper chatMessageMapper;

    public ChatMessageDTO sendMessage(String content, UUID chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Chat not found")
        );

        ChatMessage message = ChatMessage.builder()
                .text(content)
                .createdAt(LocalDateTime.now())
                .type(MessageType.INPUT)
                .chat(chat)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        Map<String, Object> payload = new HashMap<>();
        payload.put("sessionId", chat.getId());
        payload.put("chatInput", content);

        n8nController.triggerN8nWorkflow(payload);

        return chatMessageMapper.toDTO(savedMessage);
    }
}
