package com.example.planteonAiSpring.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JWTUtils {
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final Long ACCESS_EXPIRATION_TIME = 8 * 3600000L; // 8 godzin
    private static final Long REFRESH_EXPIRATION_TIME = 30 * 86400000L; // 30 dni

    /** This HashSet will be replaced by the database or cache **/
    private final Set<String> invalidatedTokens = Collections.synchronizedSet(new HashSet<>());

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("token_type", "access");

        Date expirationTime = new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expirationTime)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Date expirationTime = new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME);
        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "refresh");

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expirationTime)
                .signWith(key)
                .compact();
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && !isTokenInvalidated(token));
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    public boolean isAccessToken(String token) {
        return "access".equals(extractClaims(token, claims -> claims.get("token_type")));
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(extractClaims(token, claims -> claims.get("token_type")));
    }

    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }
}
