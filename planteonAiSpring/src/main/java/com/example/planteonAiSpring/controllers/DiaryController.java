package com.example.planteonAiSpring.controllers;

import com.example.planteonAiSpring.dtos.DiaryDTO;
import com.example.planteonAiSpring.services.DiaryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/diary")
@AllArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    @GetMapping("/get-all")
    public ResponseEntity<List<DiaryDTO>> getAllDiaries (Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok().body(diaryService.getAllDiaries(username));
    }
}
