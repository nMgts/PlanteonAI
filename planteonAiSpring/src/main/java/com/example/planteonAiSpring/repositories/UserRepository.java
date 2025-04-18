package com.example.planteonAiSpring.repositories;

import com.example.planteonAiSpring.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
