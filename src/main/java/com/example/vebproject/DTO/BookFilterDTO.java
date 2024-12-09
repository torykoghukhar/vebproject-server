package com.example.vebproject.DTO;

import lombok.Data;
import java.util.List;

@Data
public class BookFilterDTO {
    private String publisher;
    private String condition;
    private Double minPrice;
    private Double maxPrice;
    private String search;
    private String genre;
    private String author;
}



