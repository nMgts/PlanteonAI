package com.example.planteonAiSpring.controllers;

import com.example.planteonAiSpring.dtos.ChatMessageDTO;
import com.example.planteonAiSpring.services.ChatMessageService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message")
@AllArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @GetMapping("/{chatId}/get-all")
    public List<ChatMessageDTO> getAllChatMessages(@PathVariable UUID chatId, Authentication authentication) {
        return chatMessageService.getAllChatMessages(chatId, authentication);
    }

    @PostMapping("/{chatId}/send")
    public ChatMessageDTO sendMessage(@PathVariable UUID chatId, @RequestBody String content) {
        return chatMessageService.sendMessage(content, chatId);
    }
}
