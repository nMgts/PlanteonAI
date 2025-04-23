package com.example.planteonAiSpring.repositories;

import com.example.planteonAiSpring.entities.Prompt;
import com.example.planteonAiSpring.entities.User;
import com.example.planteonAiSpring.types.PromptType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromptRepository extends JpaRepository<Prompt, Long> {
    List<Prompt> findByUser(User user);
    List<Prompt> findByType(PromptType type);
}
