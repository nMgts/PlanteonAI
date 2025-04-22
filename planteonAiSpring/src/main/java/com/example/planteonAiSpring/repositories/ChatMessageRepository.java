package com.example.planteonAiSpring.repositories;

import com.example.planteonAiSpring.entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findByChatId(UUID chatId);
}
