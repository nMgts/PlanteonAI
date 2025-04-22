package com.example.planteonAiSpring.services;

import com.example.planteonAiSpring.controllers.N8nController;
import com.example.planteonAiSpring.dtos.ChatMessageDTO;
import com.example.planteonAiSpring.entities.Chat;
import com.example.planteonAiSpring.entities.ChatMessage;
import com.example.planteonAiSpring.entities.User;
import com.example.planteonAiSpring.mappers.ChatMessageMapper;
import com.example.planteonAiSpring.repositories.ChatMessageRepository;
import com.example.planteonAiSpring.repositories.ChatRepository;
import com.example.planteonAiSpring.repositories.UserRepository;
import com.example.planteonAiSpring.types.MessageType;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final N8nController n8nController;
    private final ChatMessageMapper chatMessageMapper;

    @Transactional
    public List<ChatMessageDTO> getAllChatMessages(UUID chatId, Authentication authentication) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new EntityNotFoundException("Chat not found")
        );
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
        if (!chat.getUser().equals(user)) {
            throw new SecurityException("You are not chat owner");
        }

        List<ChatMessage> messages = chatMessageRepository.findByChatId(chatId);

        return messages.stream()
                .map(chatMessageMapper::toDTO)
                .collect(Collectors.toList());
    }

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

        ResponseEntity<?> response = n8nController.triggerN8nWorkflow(payload);
        String n8nResponse = (String) response.getBody();

        ChatMessage outputMessage = ChatMessage.builder()
                .text(n8nResponse)
                .createdAt(LocalDateTime.now())
                .type(MessageType.OUTPUT)
                .chat(chat)
                .build();

        ChatMessage savedOutputMessage = chatMessageRepository.save(outputMessage);

        return chatMessageMapper.toDTO(savedOutputMessage);
    }

    private String extractOutputFromN8nResponse(String n8nResponse) {
        // Parsowanie odpowiedzi JSON i wyciąganie tekstu przepisu
        // Załóżmy, że n8nResponse to String JSON-a, który ma pole "output"
        // Możemy tu użyć np. biblioteki Jackson, Gson, itp.
        try {
            // Parsujemy odpowiedź JSON i pobieramy wartość pola "output"
            JSONObject jsonResponse = new JSONObject(n8nResponse);
            return jsonResponse.getString("output");
        } catch (Exception e) {
            throw new RuntimeException("Error extracting recipe from n8n response", e);
        }
    }
}
