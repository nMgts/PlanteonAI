package com.example.planteonAiSpring.services;

import com.example.planteonAiSpring.entities.User;
import com.example.planteonAiSpring.repositories.UserRepository;
import com.example.planteonAiSpring.requests.LoginRequest;
import com.example.planteonAiSpring.responses.LoginResponse;
import com.example.planteonAiSpring.types.Role;
import com.example.planteonAiSpring.utils.JWTUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;

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

    public LoginResponse login(LoginRequest loginRequest) {
        LoginResponse loginResponse = new LoginResponse();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            User user = userRepository.findByEmail(loginRequest.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Bad Credentials"));

            String accessToken = jwtUtils.generateAccessToken(user);
            String refreshToken = jwtUtils.generateRefreshToken(user);

            loginResponse.setStatusCode(200);
            loginResponse.setMessage("User successfully logged in");
            loginResponse.setAccessToken(accessToken);
            loginResponse.setRefreshToken(refreshToken);
            loginResponse.setEmail(user.getEmail());
            loginResponse.setFirstName(user.getFirstName());
            loginResponse.setLastName(user.getLastName());
        } catch (BadCredentialsException e) {
            loginResponse.setStatusCode(401);
            loginResponse.setError("Bad Credentials");
        } catch (Exception e) {
            loginResponse.setStatusCode(500);
            loginResponse.setError("Internal Server Error");
        }
        return loginResponse;
    }
}
