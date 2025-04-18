package com.example.planteonAiSpring.repositories;

import com.example.planteonAiSpring.entities.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
}
