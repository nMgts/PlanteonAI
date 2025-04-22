package com.example.planteonAiSpring.repositories;

import com.example.planteonAiSpring.entities.Prompt;
import com.example.planteonAiSpring.entities.User;
import com.example.planteonAiSpring.types.PromptType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
    List<Prompt> findByUser(User user);
    List<Prompt> findByType(PromptType type);
}
