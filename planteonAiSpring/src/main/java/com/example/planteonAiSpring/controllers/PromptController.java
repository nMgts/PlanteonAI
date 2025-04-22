package com.example.planteonAiSpring.controllers;

import com.example.planteonAiSpring.dtos.PromptDTO;
import com.example.planteonAiSpring.services.PromptService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prompt")
@AllArgsConstructor
public class PromptController {
    private final PromptService promptService;

    @GetMapping("/get-all")
    public ResponseEntity<List<PromptDTO>> getAllPrompts(Authentication authentication) {
        return ResponseEntity.ok(promptService.getAllPrompts(authentication));
    }

    @GetMapping("/get-user")
    public ResponseEntity<List<PromptDTO>> getUserPrompts(Authentication authentication) {
        return ResponseEntity.ok(promptService.getUserPrompts(authentication));
    }

    @GetMapping("/get-system")
    public ResponseEntity<List<PromptDTO>> getSystemPrompts() {
        return ResponseEntity.ok(promptService.getSystemPrompts());
    }

    @PostMapping("/add-with-user-type")
    public ResponseEntity<PromptDTO> addWithUserType(Authentication authentication, @RequestBody PromptDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(promptService.createUserPrompt(dto, authentication));
    }

    @PostMapping("/add-with-system-type")
    public ResponseEntity<PromptDTO> addWithSystemType(@RequestBody PromptDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(promptService.createSystemPrompt(dto));
    }

    @PutMapping("/update")
    public ResponseEntity<PromptDTO> update(@RequestBody PromptDTO dto, Authentication authentication) {
        return ResponseEntity.ok(promptService.updatePrompt(dto, authentication));
    }

    @DeleteMapping("/delete/{promptId}")
    public ResponseEntity<Void> deletePrompt(Authentication authentication, @PathVariable Long promptId) {
        promptService.deletePrompt(promptId, authentication);
        return ResponseEntity.noContent().build();
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
