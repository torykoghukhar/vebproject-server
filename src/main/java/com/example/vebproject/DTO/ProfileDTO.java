package com.example.vebproject.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProfileDTO {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String profilePictureUrl;
}
