package com.example.planteonAiSpring.mappers;

import com.example.planteonAiSpring.dtos.PromptDTO;
import com.example.planteonAiSpring.entities.Prompt;
import com.example.planteonAiSpring.entities.User;
import com.example.planteonAiSpring.types.PromptType;
import org.springframework.stereotype.Component;

@Component
public class PromptMapper {

    public static PromptDTO toDTO(Prompt prompt) {
        return PromptDTO.builder()
                .id(prompt.getId())
                .name(prompt.getName())
                .description(prompt.getDescription())
                .type(prompt.getType().name())
                .prompt(prompt.getPrompt())
                .build();
    }

    public static Prompt toEntity(PromptDTO dto, PromptType type, User user) {
        Prompt prompt = new Prompt();
        prompt.setName(dto.getName());
        prompt.setDescription(dto.getDescription());
        prompt.setType(PromptType.USER);
        prompt.setPrompt(dto.getPrompt());
        prompt.setUser(user);
        prompt.setType(type);
        return prompt;
    }
}
