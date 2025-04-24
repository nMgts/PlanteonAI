package com.example.planteonAiSpring.controllers;

import com.example.planteonAiSpring.services.Assistant;
import com.example.planteonAiSpring.services.AssistantService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/assistant")
@AllArgsConstructor
public class AssistantController {
    private final AssistantService assistantService;

    @GetMapping("/chat")
    public String model(@RequestParam(value = "message", defaultValue = "Hello") String message, Authentication authentication) {
        return assistantService.chat(message, authentication);
    }
}
