package com.learning.store.services;

import com.learning.store.config.JwtConfig;
import com.learning.store.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {
    private final JwtConfig jwtConfig;

    private Claims getClaims(String token) {
        return Jwts.parser()
                   .verifyWith(jwtConfig.getSecretKey())
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }

    public String generateToken(User user, long tokenExpiration) {
        return Jwts.builder()
                   .subject(user.getId().toString())
                   .claim("email", user.getEmail())
                   .claim("name", user.getName())
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + tokenExpiration * 1000))
                   .signWith(jwtConfig.getSecretKey())
                   .compact();
    }

    public String generateAccessToken(User user) {
        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    public boolean validateToken(String token) {
        try {
            var claims = getClaims(token);

            return claims.getExpiration().after(new Date());
        } catch (JwtException ex) {
            return false;
        }
    }

    public Long getIdFromToken(String token) {
        var claims = getClaims(token);

        return Long.valueOf(claims.getSubject());
    }
}
