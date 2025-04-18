package com.example.planteonAiSpring.responses;

import lombok.Data;

@Data
public class LoginResponse extends Response {
    private String email;
    private String firstName;
    private String lastName;
    private String accessToken;
    private String refreshToken;
}
