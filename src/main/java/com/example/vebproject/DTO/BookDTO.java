package com.example.vebproject.DTO;
import com.example.vebproject.models.Book;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookDTO {

    private Long id;
    private String title;
    private String author;
    private String genre;
    private String description;
    private BigDecimal price;
    private String PictureUrl;
    private String Publisher;
    private String User;
    private String condition;

    public BookDTO(Long id, String description, String title, String author, String genre, BigDecimal price, String pictureUrl, String publisher, String user, String condition) {

        this.id = id;
        this.description = description;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
        PictureUrl = pictureUrl;
        Publisher = publisher;
        User = user;
        this.condition = condition;
    }
}
