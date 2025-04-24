package com.example.planteonAiSpring.services;

import com.example.planteonAiSpring.dtos.ChatMessageDTO;
import com.example.planteonAiSpring.requests.MessageRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AssistantService {
    private final Assistant assistant;

    public String chat(String chatId, MessageRequest userMessage, Authentication authentication) {
        String email = authentication.getName();

        Map<String, Object> input = new HashMap<>();
        input.put("message", userMessage.getMessage());
        input.put("email", email);

        return assistant.chat(chatId, input);
    }
}
