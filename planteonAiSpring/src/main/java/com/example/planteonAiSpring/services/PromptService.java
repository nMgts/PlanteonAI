package com.example.planteonAiSpring.services;

import com.example.planteonAiSpring.dtos.PromptDTO;
import com.example.planteonAiSpring.entities.Prompt;
import com.example.planteonAiSpring.entities.User;
import com.example.planteonAiSpring.mappers.PromptMapper;
import com.example.planteonAiSpring.repositories.PromptRepository;
import com.example.planteonAiSpring.repositories.UserRepository;
import com.example.planteonAiSpring.types.PromptType;
import com.example.planteonAiSpring.types.Role;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PromptService {
    private PromptRepository promptRepository;
    private UserRepository userRepository;

    public List<PromptDTO> getAllPrompts(Authentication authentication) {
        List<PromptDTO> allPrompts = getSystemPrompts();
        List<PromptDTO> userPrompts = getUserPrompts(authentication);
        allPrompts.addAll(userPrompts);

        return allPrompts;
    }

    public List<PromptDTO> getUserPrompts(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        List<Prompt> prompts = promptRepository.findByUser(user);

        return prompts.stream()
                .map(PromptMapper::toDTO)
                .toList();
    }

    public List<PromptDTO> getSystemPrompts() {
        List<Prompt> prompts = promptRepository.findByType(PromptType.SYSTEM);

        return prompts.stream()
                .map(PromptMapper::toDTO)
                .toList();
    }

    @Transactional
    public PromptDTO createSystemPrompt(PromptDTO dto) {
        Prompt prompt = PromptMapper.toEntity(dto, PromptType.SYSTEM, null);
        Prompt saved = promptRepository.save(prompt);
        return PromptMapper.toDTO(saved);
    }

    @Transactional
    public PromptDTO createUserPrompt(PromptDTO dto, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Prompt prompt = PromptMapper.toEntity(dto, PromptType.USER, user);
        Prompt saved = promptRepository.save(prompt);
        return PromptMapper.toDTO(saved);
    }

    @Transactional
    public PromptDTO updatePrompt(PromptDTO dto, Authentication authentication) {
        Prompt prompt = promptRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Prompt not found"));
        User user = userRepository.findByEmail("User not found").orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        if (prompt.getType() == PromptType.USER) {
            String email = authentication.getName();
            if (!prompt.getUser().getEmail().equals(email)) {
                throw new SecurityException("You are not the owner of this prompt");
            }
        } else {
            if (!user.getRole().equals(Role.ADMIN)) {
                throw new SecurityException("You are not authorized to update this prompt");
            }
        }

        Prompt newPrompt;
        if (dto.getType().equals(PromptType.USER.toString())) {
            newPrompt = PromptMapper.toEntity(dto, PromptType.USER, user);
        } else {
            newPrompt = PromptMapper.toEntity(dto, PromptType.SYSTEM, null);
        }
        newPrompt.setId(dto.getId());
        promptRepository.save(newPrompt);
        return PromptMapper.toDTO(newPrompt);
    }

    @Transactional
    public void deletePrompt(Long promptId, Authentication authentication) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new EntityNotFoundException("Prompt not found"));
        User user = userRepository.findByEmail("User not found").orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        // Sprawdzamy, czy użytkownik ma prawo usunąć prompt (tylko dla typu USER)
        if (prompt.getType() == PromptType.USER) {
            String email = authentication.getName();
            if (!prompt.getUser().getEmail().equals(email)) {
                throw new SecurityException("You are not the owner of this prompt");
            }
        } else {
            if (!user.getRole().equals(Role.ADMIN)) {
                throw new SecurityException("You are not authorized to delete this prompt");
            }
        }

        promptRepository.delete(prompt);
    }
}
