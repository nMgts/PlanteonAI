package com.example.planteonAiSpring.controllers;

import com.example.planteonAiSpring.requests.MessageRequest;
import com.example.planteonAiSpring.services.AssistantService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/assistant")
@AllArgsConstructor
public class AssistantController {
    private final AssistantService assistantService;

    @GetMapping(value = "/chat/{chatId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@PathVariable String chatId, @RequestParam String userMessage) {
        return assistantService.chat(chatId, userMessage);
    }
}
