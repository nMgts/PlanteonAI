package com.example.planteonAiSpring.tools;

import com.example.planteonAiSpring.dtos.DiaryDTO;
import com.example.planteonAiSpring.services.DiaryService;
import dev.langchain4j.agent.tool.Tool;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DiaryTools {
    private final DiaryService diaryService;

    @Tool
    public DiaryDTO createDiary(DiaryDTO diaryDTO, String email) {
        return diaryService.createDiary(diaryDTO, email);
    }

    @Tool
    public List<DiaryDTO> getAllDiaries (String email) {
        return diaryService.getAllDiaries(email);
    }
}
