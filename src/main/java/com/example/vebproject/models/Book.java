package com.example.vebproject.models;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;


@NoArgsConstructor
@Entity
@Data
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String genre;
    private String description;
    private BigDecimal price;
    private String PictureUrl;
    public String condition;


    private boolean isSold = false;

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", PictureUrl='" + PictureUrl + '\'' +
                ", condition='" + condition + '\'' +
                ", isSold=" + isSold +
                '}';
    }

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private List<Review> reviews;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    @JsonBackReference
    private Publisher publisher;

    public Book(User user, boolean isSold, String condition, String pictureUrl, BigDecimal price, String description, String genre, String author, String title) {
        this.user = user;
        this.isSold = isSold;
        this.condition = condition;
        PictureUrl = pictureUrl;
        this.price = price;
        this.description = description;
        this.genre = genre;
        this.author = author;
        this.title = title;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> orderitems;
}

