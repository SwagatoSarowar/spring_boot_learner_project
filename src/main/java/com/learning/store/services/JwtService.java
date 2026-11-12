package com.learning.store.services;

import com.learning.store.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    @Value("${spring.jwt.secret}")
    private String secret;

    @Value("${spring.jwt.token-expiration}")
    private Long tokenExpiration;

    private Claims getClaims(String token) {
        return Jwts.parser()
                   .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }

    public String generateToken(User user) {
        return Jwts.builder()
                   .subject(user.getId().toString())
                   .claim("email", user.getEmail())
                   .claim("name", user.getName())
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + tokenExpiration * 1000))
                   .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                   .compact();
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
