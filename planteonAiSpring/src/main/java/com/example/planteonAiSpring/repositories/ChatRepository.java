package com.example.planteonAiSpring.repositories;

import com.example.planteonAiSpring.entities.Chat;
import com.example.planteonAiSpring.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    List<Chat> findAllByUser(User user);
}
