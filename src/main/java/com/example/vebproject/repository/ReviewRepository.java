package com.example.vebproject.repository;
import com.example.vebproject.DTO.ReviewDTO;
import com.example.vebproject.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBook_Id(Long bookId);
}
