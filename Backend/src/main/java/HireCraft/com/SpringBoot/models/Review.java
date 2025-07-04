package HireCraft.com.SpringBoot.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Entity
@NoArgsConstructor
@Data
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double ratingNo;

    private String reviewTxt;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientProfile clientProfile;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ServiceProviderProfile providerProfile;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getRatingNo() {
        return ratingNo;
    }

    public void setRatingNo(Double ratingNo) {
        this.ratingNo = ratingNo;
    }

    public String getReviewTxt() {
        return reviewTxt;
    }

    public void setReviewTxt(String reviewTxt) {
        this.reviewTxt = reviewTxt;
    }

    public ClientProfile getClientProfile() {
        return clientProfile;
    }

    public void setClientProfile(ClientProfile clientProfile) {
        this.clientProfile = clientProfile;
    }

    public ServiceProviderProfile getProviderProfile() {
        return providerProfile;
    }

    public void setProviderProfile(ServiceProviderProfile providerProfile) {
        this.providerProfile = providerProfile;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
