package HireCraft.com.SpringBoot.dtos.response;

import HireCraft.com.SpringBoot.enums.PaymentStatus;
import HireCraft.com.SpringBoot.models.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private Long clientId;
    private Long providerId;
    private Long bookingId;
    private BigDecimal totalAmount;
    private BigDecimal platformFee;
    private BigDecimal providerAmount;
    private PaymentStatus status;
    private String paymentMethod;
    private String externalTransactionId;
    private String description;
    private LocalDateTime createdAt;

    public static PaymentResponse fromEntity(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .clientId(payment.getClientId())
                .providerId(payment.getProviderId())
                .bookingId(payment.getBookingId())
                .totalAmount(payment.getTotalAmount())
                .platformFee(payment.getPlatformFee())
                .providerAmount(payment.getProviderAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .description(payment.getDescription())
                .externalTransactionId(payment.getExternalTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
