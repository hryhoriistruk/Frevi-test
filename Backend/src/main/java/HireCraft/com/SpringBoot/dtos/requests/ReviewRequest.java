package HireCraft.com.SpringBoot.dtos.requests;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {
    @DecimalMin(value = "1.0", message = "Rating must be at least 1 star")
    @DecimalMax(value = "5.0", message = "Rating cannot be more than 5 stars")
    private Double rating;

    @NotBlank(message = "Enter a review")
    private String reviewTxt;

    private Long providerId;

    public @DecimalMin(value = "1.0", message = "Rating must be at least 1 star") @DecimalMax(value = "5.0", message = "Rating cannot be more than 5 stars") Double getRating() {
        return rating;
    }

    public void setRating(@DecimalMin(value = "1.0", message = "Rating must be at least 1 star") @DecimalMax(value = "5.0", message = "Rating cannot be more than 5 stars") Double rating) {
        this.rating = rating;
    }

    public @NotBlank(message = "Enter a review") String getReviewTxt() {
        return reviewTxt;
    }

    public void setReviewTxt(@NotBlank(message = "Enter a review") String reviewTxt) {
        this.reviewTxt = reviewTxt;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }
}
