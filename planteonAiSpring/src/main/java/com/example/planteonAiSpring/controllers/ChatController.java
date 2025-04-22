package com.example.planteonAiSpring.controllers;

import com.example.planteonAiSpring.dtos.ChatDTO;
import com.example.planteonAiSpring.services.ChatService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/create")
    public ResponseEntity<ChatDTO> createChat(@RequestParam String title, Authentication authentication) {
        ChatDTO createdChat = chatService.createChat(title, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChat);
    }

    @PutMapping("/rename")
    public ResponseEntity<ChatDTO> renameChat(@RequestBody ChatDTO dto, Authentication authentication) {
        ChatDTO updatedChat = chatService.renameChat(dto, authentication);
        return ResponseEntity.ok(updatedChat);
    }

    @DeleteMapping("/delete/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable UUID chatId, Authentication authentication) {
        chatService.deleteChat(chatId, authentication);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ChatDTO>> getAllChats(Authentication authentication) {
        List<ChatDTO> chats = chatService.getAllChats(authentication);
        return ResponseEntity.ok(chats);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurity(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}
