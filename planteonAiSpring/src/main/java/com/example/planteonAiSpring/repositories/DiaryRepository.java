package com.example.planteonAiSpring.repositories;

import com.example.planteonAiSpring.entities.Diary;
import com.example.planteonAiSpring.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Optional<Diary> findByTitle(String title);
    List<Diary> findByUser(User user);
}
