package com.example.vebproject.DTO;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;
    private Long bookId;
    private int rating;
    private String comment;
    private String username;
}
