package com.example.vebproject.services;

import com.example.vebproject.DTO.ReviewDTO;
import com.example.vebproject.models.Book;
import com.example.vebproject.models.Review;
import com.example.vebproject.models.User;
import com.example.vebproject.repository.BookRepository;
import com.example.vebproject.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(BookRepository bookRepository, ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
    }

    public boolean createReview(ReviewDTO reviewDTO, User user ) {
        Book book = bookRepository.findById(reviewDTO.getBookId()).orElse(null);
        if (book == null) {
            return false;
        }
        Date currentDate = new Date();
        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setCreatedAt(currentDate);
        reviewRepository.save(review);
        return true;
    }

    public List<ReviewDTO> getReviewsForBook(Long bookId) {
        return reviewRepository.findByBook_Id(bookId).stream().map(review -> {
            ReviewDTO reviewDTO = new ReviewDTO();
            reviewDTO.setId(review.getId());
            reviewDTO.setBookId(review.getBook().getId());
            reviewDTO.setRating(review.getRating());
            reviewDTO.setComment(review.getComment());
            reviewDTO.setUsername(review.getUser().getName());
            return reviewDTO;
        }
        ).collect(Collectors.toList());
    }

    public boolean deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null) {
            return false;
        }
        reviewRepository.delete(review);
        return true;
    }
}
