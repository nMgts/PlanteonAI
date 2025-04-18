package com.example.planteonAiSpring.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RefreshTokenResponse extends Response {
    private String accessToken;
    private String refreshToken;
}
