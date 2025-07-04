package HireCraft.com.SpringBoot.repository;

import HireCraft.com.SpringBoot.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByClientProfile_Id(Long clientId);
    List<Review> findByProviderProfile_Id(Long providerId);
    List<Review> findByClientProfile_IdAndProviderProfile_Id(Long clientId, Long providerId);
    long countByProviderProfile_Id(Long providerId);
}
