package com.example.planteonAiSpring.controllers;

import com.example.planteonAiSpring.requests.MessageRequest;
import com.example.planteonAiSpring.services.AssistantService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assistant")
@AllArgsConstructor
public class AssistantController {
    private final AssistantService assistantService;

    @GetMapping("/chat/{chatId}")
    public String model(@PathVariable String chatId, @RequestBody MessageRequest request, Authentication authentication) {
        return assistantService.chat(chatId, request, authentication);
    }
}
