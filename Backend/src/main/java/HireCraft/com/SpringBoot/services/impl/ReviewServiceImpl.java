package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.NotificationRequest;
import HireCraft.com.SpringBoot.dtos.requests.ReviewRequest;
import HireCraft.com.SpringBoot.dtos.response.BookingResponse;
import HireCraft.com.SpringBoot.dtos.response.ReviewResponse;
import HireCraft.com.SpringBoot.enums.NotificationType;
import HireCraft.com.SpringBoot.enums.ReferenceType;
import HireCraft.com.SpringBoot.models.*;
import HireCraft.com.SpringBoot.repository.ClientProfileRepository;
import HireCraft.com.SpringBoot.repository.ReviewRepository;
import HireCraft.com.SpringBoot.repository.ServiceProviderProfileRepository;
import HireCraft.com.SpringBoot.repository.UserRepository;
import HireCraft.com.SpringBoot.services.NotificationService;
import HireCraft.com.SpringBoot.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
//@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final ServiceProviderProfileRepository serviceProviderProfileRepository;
    private final NotificationService notificationService;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository, ClientProfileRepository clientProfileRepository, ServiceProviderProfileRepository serviceProviderProfileRepository, NotificationService notificationService) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.serviceProviderProfileRepository = serviceProviderProfileRepository;
        this.notificationService = notificationService;
    }

    @Override
    public ReviewResponse createReview(ReviewRequest request, UserDetails userDetails) {
        // Get authenticated user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get client profile from user email
        ClientProfile clientProfile = clientProfileRepository.findByUserEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        // Get service provider profile
        ServiceProviderProfile serviceProviderProfile = serviceProviderProfileRepository.findById(request.getProviderId())
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        // Build review
        Review review = Review.builder()
                .ratingNo(request.getRating())
                .reviewTxt(request.getReviewTxt())
                .clientProfile(clientProfile)
                .providerProfile(serviceProviderProfile)
                .build();

        // Save and return response
        Review savedReview = reviewRepository.save(review);
        updateServiceProviderAverageRating(serviceProviderProfile);

        Long providerUserId = serviceProviderProfile.getUser().getId();

        // Create a notification for the provider
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .message(String.format("New %.1f-star review received from %s.",
                        savedReview.getRatingNo(),
                        clientProfile.getUser().getFirstName() + " " + clientProfile.getUser().getLastName()))
                .type(NotificationType.REVIEW_RECEIVED)
                .userId(providerUserId)
                .referenceId(savedReview.getId()) // Reference the new review's ID
                .referenceType(ReferenceType.REVIEW) // Indicate reference type is REVIEW
                .build();
        notificationService.createNotification(notificationRequest);

        return ReviewResponse.builder()
                .rating(savedReview.getRatingNo())
                .reviewTxt(savedReview.getReviewTxt())
                .clientFullName(user.getFirstName() + " " + user.getLastName())
                .createdAt(savedReview.getCreatedAt())
                .build();
    }

    @Transactional
    private void updateServiceProviderAverageRating(ServiceProviderProfile serviceProviderProfile) {
        // Fetch all reviews for this specific service provider
        List<Review> reviews = reviewRepository.findByProviderProfile_Id(serviceProviderProfile.getId());

        if (reviews.isEmpty()) {
            serviceProviderProfile.setAverageRating(0.0); // No reviews, so average is 0
        } else {
            // Calculate the sum of all ratings
            double sumOfRatings = reviews.stream()
                    .mapToDouble(Review::getRatingNo) // Use getRatingNo from your Review model
                    .sum();

            // Calculate the raw new average
            double newAverage = sumOfRatings / reviews.size();

            double roundedAverage = Math.round(newAverage * 10.0) / 10.0;

            serviceProviderProfile.setAverageRating(roundedAverage);
        }

        serviceProviderProfileRepository.save(serviceProviderProfile);
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return convertToResponseList(reviews);
    }

    @Override
    public List<ReviewResponse> getReviewsForProvider(UserDetails userDetails) {
//        List<Review> reviews = reviewRepository.findByProviderProfile_Id(providerId);
//        return convertToResponseList(reviews);
        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        return reviewRepository.findByProviderProfile_Id(providerProfile.getId())
                .stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        User clientUser = review.getClientProfile().getUser();

        response.setRating(review.getRatingNo());
        response.setReviewTxt(review.getReviewTxt());
        response.setClientFullName(clientUser.getFirstName() + " " + clientUser.getLastName());
        response.setCreatedAt(review.getCreatedAt());

        return response;
    }

    @Override
    public List<ReviewResponse> getReviewsByClient(Long clientId) {
        List<Review> reviews = reviewRepository.findByClientProfile_Id(clientId);
        return convertToResponseList(reviews);
    }

    @Override
    public List<ReviewResponse> getReviewsByClientForProvider(Long clientId, Long providerId) {
        List<Review> reviews = reviewRepository.findByClientProfile_IdAndProviderProfile_Id(clientId, providerId);
        return convertToResponseList(reviews);
    }

    @Override
    public long getReviewCountForProvider(UserDetails userDetails) {
        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        // This is the core logic: count reviews by provider profile ID
        return reviewRepository.countByProviderProfile_Id(providerProfile.getId());
    }

    private List<ReviewResponse> convertToResponseList(List<Review> reviews) {
        return reviews.stream()
                .map(review -> {
                    User clientUser = review.getClientProfile().getUser();
                    return ReviewResponse.builder()
                            .rating(review.getRatingNo())
                            .reviewTxt(review.getReviewTxt())
                            .clientFullName(clientUser.getFirstName() + " " + clientUser.getLastName())
                            .createdAt(review.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }
}