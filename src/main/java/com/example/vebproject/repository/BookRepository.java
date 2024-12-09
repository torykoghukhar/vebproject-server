package com.example.vebproject.repository;
import org.springframework.data.jpa.repository.Query;
import com.example.vebproject.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT DISTINCT b.author FROM Book b")
    List<String> findAllDistinctAuthors();

    @Query("SELECT DISTINCT b.genre FROM Book b")
    List<String> findAllDistinctGenres();
}
