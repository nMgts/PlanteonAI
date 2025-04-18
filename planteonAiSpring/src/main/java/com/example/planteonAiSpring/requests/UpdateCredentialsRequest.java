package com.example.planteonAiSpring.requests;

import lombok.Getter;

@Getter
public class UpdateCredentialsRequest {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
