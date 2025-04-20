package com.example.planteonAiSpring.controllers;

import com.example.planteonAiSpring.requests.ChangePasswordRequest;
import com.example.planteonAiSpring.requests.LoginRequest;
import com.example.planteonAiSpring.requests.RegisterRequest;
import com.example.planteonAiSpring.requests.UpdateCredentialsRequest;
import com.example.planteonAiSpring.responses.LoginResponse;
import com.example.planteonAiSpring.responses.RefreshTokenResponse;
import com.example.planteonAiSpring.responses.Response;
import com.example.planteonAiSpring.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request);
        if (loginResponse.getStatusCode() == 200) {
            Cookie cookieRefreshToken = new Cookie("refreshToken", loginResponse.getRefreshToken());
            cookieRefreshToken.setPath("/");
            cookieRefreshToken.setMaxAge(2592000);
            response.addCookie(cookieRefreshToken);

            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(loginResponse.getStatusCode()).body(loginResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                    HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, refreshToken);
        Cookie cookieRefreshToken = new Cookie("refreshToken", "");
        cookieRefreshToken.setHttpOnly(true);
        cookieRefreshToken.setPath("/");
        cookieRefreshToken.setMaxAge(0);
        response.addCookie(cookieRefreshToken);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/refresh")
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@CookieValue(value = "refreshToken") String refreshToken,
                                                             HttpServletResponse response) {
        RefreshTokenResponse refreshResponse = authService.refreshToken(refreshToken);
        Cookie cookieRefreshToken = new Cookie("refreshToken", refreshResponse.getRefreshToken());
        cookieRefreshToken.setHttpOnly(true);
        cookieRefreshToken.setPath("/");
        cookieRefreshToken.setMaxAge(2592000);
        response.addCookie(cookieRefreshToken);
        return ResponseEntity.ok(refreshResponse);
    }

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            Response response = authService.registerNewUser(registerRequest);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PutMapping("/update-credentials")
    public ResponseEntity<?> updateUserCredentials(@RequestBody UpdateCredentialsRequest request) {
        try {
            Response response = authService.updateUserCredentials(request);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Authentication authentication) {
        try {
            Response response = authService.changePassword(authentication, request);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/is-authenticated")
    public ResponseEntity<String> isUserAuthenticated() {
        return ResponseEntity.ok().body("{\"message\": \"User is authenticated\"}");
    }
}
