package com.example.vebproject.controllers;

import com.example.vebproject.DTO.ReviewDTO;
import com.example.vebproject.models.User;
import com.example.vebproject.services.ReviewService;
import com.example.vebproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class ReviewController {
    private final UserService userService;
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @PostMapping ("/addreview")
    public ResponseEntity<?> addReview(@RequestBody ReviewDTO reviewDTO, Principal principal) {
        if (principal == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.getuser(principal.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (reviewService.createReview(reviewDTO, user)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping ("/allreviewsforbook/{bookId}")
    public ResponseEntity<?> getAllReviewsForBook(@PathVariable Long bookId) {
        List<ReviewDTO> reviewDTOList = reviewService.getReviewsForBook(bookId);
        if (reviewDTOList == null || reviewDTOList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(reviewDTOList);
    }

    @DeleteMapping ("/deletereviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        if (reviewId == null) {
            return ResponseEntity.badRequest().build();
        }
        if (reviewService.deleteReview(reviewId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
