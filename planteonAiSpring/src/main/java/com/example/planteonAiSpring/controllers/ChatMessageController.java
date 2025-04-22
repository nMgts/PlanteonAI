package com.example.planteonAiSpring.controllers;

import com.example.planteonAiSpring.dtos.ChatMessageDTO;
import com.example.planteonAiSpring.services.ChatMessageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/message")
@AllArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @PostMapping("/{chatId}/send")
    public ChatMessageDTO sendMessage(@PathVariable UUID chatId, @RequestBody String content) {
        return chatMessageService.sendMessage(content, chatId);
    }
}
