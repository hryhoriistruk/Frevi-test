package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.requests.ReviewRequest;
import HireCraft.com.SpringBoot.dtos.response.ReviewResponse;
import HireCraft.com.SpringBoot.services.ReviewService;
import HireCraft.com.SpringBoot.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
//@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/create-review")
    @PreAuthorize("hasAuthority('ADD_REVIEW')")
    public ResponseEntity<ReviewResponse> createReview(@RequestBody @Valid ReviewRequest request,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        ReviewResponse response = reviewService.createReview(request, userDetails);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('VIEW_ALL_REVIEWS')")
    public List<ReviewResponse> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/provider/me")
    @PreAuthorize("hasAuthority('VIEW_PROVIDER_REVIEWS') or hasAuthority('VIEW_ALL_REVIEWS')")
    public List<ReviewResponse> getReviewsForProvider(@AuthenticationPrincipal UserDetails userDetails) {
        return reviewService.getReviewsForProvider(userDetails);
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAuthority('VIEW_CLIENT_REVIEWS') or hasAuthority('VIEW_ALL_REVIEWS')")
    public List<ReviewResponse> getReviewsByClient(@PathVariable Long clientId) {
        return reviewService.getReviewsByClient(clientId);
    }

    @GetMapping("/provider/dashboard/total-reviews")
    @PreAuthorize("hasRole('ROLE_PROVIDER')") // Ensure only providers can access this
    public ResponseEntity<Long> getTotalReviewsForProvider(@AuthenticationPrincipal UserDetails userDetails) {
        long reviewCount = reviewService.getReviewCountForProvider(userDetails);
        return ResponseEntity.ok(reviewCount);
    }


    @GetMapping("/client/{clientId}/provider/{providerId}")
    @PreAuthorize("hasAuthority('VIEW_CLIENT_REVIEWS') or hasAuthority('VIEW_ALL_REVIEWS')")
    public List<ReviewResponse> getReviewsByClientForProvider(@PathVariable Long clientId,
                                                              @PathVariable Long providerId) {
        return reviewService.getReviewsByClientForProvider(clientId, providerId);
    }

}
