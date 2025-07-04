package HireCraft.com.SpringBoot.repository;

import HireCraft.com.SpringBoot.enums.PaymentStatus;
import HireCraft.com.SpringBoot.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByClientIdOrderByCreatedAtDesc(Long clientId);

    List<Payment> findByProviderIdOrderByCreatedAtDesc(Long providerId);

    List<Payment> findByBookingIdOrderByCreatedAtDesc(Long bookingId);

    List<Payment> findByStatusOrderByCreatedAtDesc(PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.clientId = :clientId AND p.status = :status")
    List<Payment> findByClientIdAndStatus(Long clientId, PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.providerId = :providerId AND p.status = :status")
    List<Payment> findByProviderIdAndStatus(Long providerId, PaymentStatus status);

    @Query("SELECT SUM(p.platformFee) FROM Payment p WHERE p.status = 'COMPLETED' AND p.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalPlatformRevenue(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(p.providerAmount) FROM Payment p WHERE p.providerId = :providerId AND p.status = 'COMPLETED' AND p.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateProviderEarnings(Long providerId, LocalDateTime startDate, LocalDateTime endDate);
}
