package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.PaymentBreakdown;
import HireCraft.com.SpringBoot.dtos.requests.PaymentRequest;
import HireCraft.com.SpringBoot.dtos.response.PaymentResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    // UserDetails-based methods (authenticated user context)
    PaymentResponse processPayment(PaymentRequest request, UserDetails userDetails);
    PaymentResponse getPaymentById(Long paymentId, UserDetails userDetails);
    List<PaymentResponse> getClientPayments(UserDetails userDetails);
    List<PaymentResponse> getProviderPayments(UserDetails userDetails);
    List<PaymentResponse> getBookingPayments(Long bookingId, UserDetails userDetails);
    BigDecimal calculateProviderEarnings(Long providerId, LocalDateTime startDate,
                                         LocalDateTime endDate, UserDetails userDetails);

    // Non-UserDetails methods (for admin access or backward compatibility)
    PaymentResponse processPayment(PaymentRequest request);
    PaymentResponse getPaymentById(Long paymentId);
    List<PaymentResponse> getClientPayments(Long clientId);
    List<PaymentResponse> getProviderPayments(Long providerId);
    List<PaymentResponse> getBookingPayments(Long bookingId);
    BigDecimal calculateProviderEarnings(Long providerId, LocalDateTime startDate, LocalDateTime endDate);

    // Utility methods
    PaymentBreakdown calculatePaymentBreakdown(BigDecimal amount);
    BigDecimal calculatePlatformRevenue(LocalDateTime startDate, LocalDateTime endDate);
}