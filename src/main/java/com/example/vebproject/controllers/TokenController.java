package com.example.vebproject.controllers;
import com.example.vebproject.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TokenController {
    private final JwtUtils jwtUtils;
    @Autowired
    public TokenController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/valid-token")
    public ResponseEntity<?> validtoken(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }
        boolean result = jwtUtils.isValidUserToken(token);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isValid", result);
        return ResponseEntity.ok(response);
    }
}
