package com.example.planteonAiSpring.services;

import com.example.planteonAiSpring.entities.User;
import com.example.planteonAiSpring.exceptions.EmailTakenException;
import com.example.planteonAiSpring.repositories.UserRepository;
import com.example.planteonAiSpring.requests.ChangePasswordRequest;
import com.example.planteonAiSpring.requests.LoginRequest;
import com.example.planteonAiSpring.requests.RegisterRequest;
import com.example.planteonAiSpring.requests.UpdateCredentialsRequest;
import com.example.planteonAiSpring.responses.LoginResponse;
import com.example.planteonAiSpring.responses.RefreshTokenResponse;
import com.example.planteonAiSpring.responses.Response;
import com.example.planteonAiSpring.types.Role;
import com.example.planteonAiSpring.utils.JWTUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;

    /***  ONLY FOR FIRST ADMIN  **/
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
            authenticationManager.authenticate(
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

    /*** ONLY ADMIN CAN REGISTER NEW USERS **/
    public Response registerNewUser(RegisterRequest registerRequest) {
        Response response = new Response();

        try {
            Optional<User> opt = userRepository.findByEmail(registerRequest.getEmail());
            if (opt.isPresent()) {
                throw new EmailTakenException("Email is taken");
            }

            User user = new User();
            user.setEmail(registerRequest.getEmail());
            user.setRole(Role.USER);
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setDeleted(false);

            userRepository.save(user);

            response.setStatusCode(201);
            response.setMessage("User registered successfully");
        } catch (EmailTakenException e) {
            response.setStatusCode(409);
            response.setError(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError("Internal Server Error");
        }
        return response;
    }

    /*** ONLY ADMIN CAN CHANGE USER CREDENTIALS WITHOUT USER PASSWORD **/
    public Response updateUserCredentials(UpdateCredentialsRequest request) {
        Response response = new Response();

        try {
            Optional<User> opt = userRepository.findByEmail(request.getEmail());
            if (opt.isPresent() && opt.get().getId() != request.getId()) {
                throw new EmailTakenException("Email is taken by another user");
            }

            User user = userRepository.findById(request.getId()).orElseThrow(
                    () -> new EntityNotFoundException("User not found"));

            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());

            userRepository.save(user);

            if (!request.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            response.setStatusCode(200);
            response.setMessage("Credentials updated successfully");
        } catch (EmailTakenException e) {
            response.setStatusCode(409);
            response.setError(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError("Internal server error");
        }
        return response;
    }

    /*** ONLY FOR AUTHENTICATED USERS **/
    public Response changePassword(Authentication authentication, ChangePasswordRequest request) {
        Response response = new Response();

        try {
            User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
                    () -> new EntityNotFoundException("User not found")
            );

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authentication.getName(),
                            request.getOldPassword()
                    )
            );

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            response.setStatusCode(200);
            response.setMessage("Password changed successfully");
        } catch (BadCredentialsException e) {
            response.setStatusCode(401);
            response.setError("Bad Credentials");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError("Internal server error");
        }

        return response;
    }

    public RefreshTokenResponse refreshToken(String refreshToken) {
        RefreshTokenResponse response = new RefreshTokenResponse();

        try {
            String email = jwtUtils.extractUsername(refreshToken);
            User user = userRepository.findByEmail(email).orElseThrow(
                    () -> new EntityNotFoundException("User not found")
            );

            if (jwtUtils.isTokenValid(refreshToken, user) && jwtUtils.isRefreshToken(refreshToken)) {
                String newAccessToken = jwtUtils.generateAccessToken(user);
                String newRefreshToken = jwtUtils.generateRefreshToken(user);

                response.setStatusCode(200);
                response.setMessage("Token refreshed successfully");
                response.setAccessToken(newAccessToken);
                response.setRefreshToken(newRefreshToken);

                jwtUtils.invalidateToken(refreshToken);
            } else {
                response.setStatusCode(401);
                response.setError("Refresh token is not valid");
            }

            return response;
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError("Internal server error");
            return response;
        }
    }

    public void logout(HttpServletRequest request, String refreshToken) {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            jwtUtils.invalidateToken(accessToken);
        }

        if (refreshToken != null && !refreshToken.isBlank()) {
            jwtUtils.invalidateToken(refreshToken);
        }

        SecurityContextHolder.clearContext();
    }
}
