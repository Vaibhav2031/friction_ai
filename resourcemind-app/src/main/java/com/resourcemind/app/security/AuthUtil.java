package com.resourcemind.app.security;

import javax.crypto.SecretKey;

import com.resourcemind.app.api.dto.LoginRequest;
import com.resourcemind.app.api.dto.LoginResponse;
import com.resourcemind.app.api.dto.SignupResponse;
import com.resourcemind.app.domain.User;
import com.resourcemind.app.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class AuthUtil {

    private final String jwtSecretKey;

    public AuthUtil(@Value("${jwt.secretKey}") String jwtSecretKey) {
        this.jwtSecretKey = jwtSecretKey;
    }

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + 3600000); // Token valid for 1 hour

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(now.getTime() + 60_000)) // Token valid for 60 seconds
                .signWith(getSecretKey())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();

    }
}
