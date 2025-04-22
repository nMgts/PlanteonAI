package com.example.planteonAiSpring.services;

import com.example.planteonAiSpring.dtos.ChatDTO;
import com.example.planteonAiSpring.entities.Chat;
import com.example.planteonAiSpring.entities.User;
import com.example.planteonAiSpring.mappers.ChatMapper;
import com.example.planteonAiSpring.repositories.ChatRepository;
import com.example.planteonAiSpring.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public ChatDTO createChat(String title, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        Chat chat = Chat.builder()
                .title(title)
                .user(user)
                .build();
        return ChatMapper.toDto(chatRepository.save(chat));
    }

    public ChatDTO renameChat(ChatDTO dto, Authentication authentication) {
        Chat chat = chatRepository.findById(dto.getId()).orElseThrow(
                () -> new EntityNotFoundException("Chat not found")
        );
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        if (!chat.getUser().equals(user)) {
            throw new SecurityException("You are not chat owner");
        }

        chat.setTitle(dto.getTitle());
        return ChatMapper.toDto(chatRepository.save(chat));
    }

    public void deleteChat(UUID chatId, Authentication authentication) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!chat.getUser().equals(user)) {
            throw new SecurityException("You are not the owner of this chat");
        }

        chatRepository.delete(chat);
    }

    public List<ChatDTO> getAllChats(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return chatRepository.findAllByUser(user).stream()
                .map(ChatMapper::toDto)
                .toList();
    }
}
