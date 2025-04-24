package com.example.planteonAiSpring.services;

import com.example.planteonAiSpring.dtos.DiaryDTO;
import com.example.planteonAiSpring.entities.Diary;
import com.example.planteonAiSpring.entities.User;
import com.example.planteonAiSpring.mappers.DiaryMapper;
import com.example.planteonAiSpring.repositories.DiaryRepository;
import com.example.planteonAiSpring.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    public List<DiaryDTO> getAllDiaries (String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User not fund")
        );

        List<Diary> diaries = diaryRepository.findByUser(user);
        return diaries.stream()
                .map(DiaryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public DiaryDTO createDiary (DiaryDTO diaryDTO, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("User not fund")
        );

        Diary diary = new Diary();
        diary.setUser(user);
        diary.setTitle(diaryDTO.getTitle());

        diaryRepository.save(diary);
        return DiaryMapper.toDTO(diary);
    }
}
