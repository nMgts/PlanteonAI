package com.example.planteonAiSpring.services;

import com.example.planteonAiSpring.entities.User;
import com.example.planteonAiSpring.repositories.UserRepository;
import com.example.planteonAiSpring.types.Role;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@AllArgsConstructor
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerAdmin(String firstName, String lastName, String email, String password) {
        User user = new User();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.ADMIN);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setDeleted(false);
        userRepository.save(user);

        logger.info("Registered admin: " + user.getFirstName() + " " + user.getLastName() + " " + user.getEmail());
    }
}
