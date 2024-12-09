package com.example.vebproject.DTO;
import lombok.Data;

@Data
public class SignInRequest {
    private String name;
    private String passwordHash;
}
