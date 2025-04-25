package com.example.planteonAiSpring.controllers;

import com.example.planteonAiSpring.requests.MessageRequest;
import com.example.planteonAiSpring.services.AssistantService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/assistant")
@AllArgsConstructor
public class AssistantController {
    private final AssistantService assistantService;

    @GetMapping("/chat/{chatId}")
    public SseEmitter model(@PathVariable String chatId,
                            //@RequestBody MessageRequest request,
                            Authentication authentication) {
        return assistantService.chat(chatId, authentication);
    }
}
