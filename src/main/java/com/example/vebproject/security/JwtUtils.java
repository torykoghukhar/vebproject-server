package com.example.vebproject.security;

import com.example.vebproject.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private final UserRepository userRepository;
    @Value("${jwt.secret}")
    public String secret;

    @Value("${jwt.expiration-time}")
    public Long lifeTime;

    private SecretKey key;

    public JwtUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public void init() {
        if (secret != null) {
            this.key = Keys.hmacShaKeyFor(secret.getBytes());
        } else {
            throw new IllegalArgumentException("JWT secret cannot be null");
        }
    }

    public String generateToken(Authentication auth) {
        if (key == null) {
            init();
        }

        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + lifeTime);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .issuedAt(new Date())
                .expiration(expireDate)
                .signWith(key)
                .compact();
    }

    public String getNameFromToken(String token) {
        if (key == null) {
            init();
        }
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean isValidToken(String token) {
        if (key == null) {
            init();
        }
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parse(token);
            return true;
        } catch (JwtException e) {
            System.out.println( e.getMessage());
        }
        return false;
    }

    public boolean isValidUserToken(String token) {
        if (isValidToken(token)) {
            String username = getNameFromToken(token);
            if (username != null) {
                return userRepository.existsByName(username);
            }
        }
        return false;
    }

}
