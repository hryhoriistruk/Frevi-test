package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.ReviewRequest;
import HireCraft.com.SpringBoot.dtos.response.ReviewResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest request, UserDetails userDetails);

    List<ReviewResponse> getAllReviews();  // All reviews in app

//    List<ReviewResponse> getReviewsForProvider(Long providerId);

    List<ReviewResponse> getReviewsForProvider(UserDetails userDetails);

    List<ReviewResponse> getReviewsByClient(Long clientId); // All reviews written by a specific client

    List<ReviewResponse> getReviewsByClientForProvider(Long clientId, Long providerId); // A client’s review(s) for a specific provider

    long getReviewCountForProvider(UserDetails userDetails);
}
