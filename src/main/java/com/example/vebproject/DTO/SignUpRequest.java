package com.example.vebproject.DTO;
import lombok.Data;

@Data
public class SignUpRequest {
    private String name;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
}
