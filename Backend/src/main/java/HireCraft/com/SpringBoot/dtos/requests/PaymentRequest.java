package HireCraft.com.SpringBoot.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequest {
    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Provider ID is required")
    private Long providerId;

    private Long bookingId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Amount format is invalid")
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotBlank(message = "Payment method ID is required")
    private String paymentMethodId; // Stripe payment method ID

    private String description;

    public @NotNull(message = "Client ID is required") Long getClientId() {
        return clientId;
    }

    public @NotNull(message = "Provider ID is required") Long getProviderId() {
        return providerId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than 0") @Digits(integer = 8, fraction = 2, message = "Amount format is invalid") BigDecimal getAmount() {
        return amount;
    }

    public @NotBlank(message = "Payment method is required") String getPaymentMethod() {
        return paymentMethod;
    }

    public @NotBlank(message = "Payment method ID is required") String getPaymentMethodId() {
        return paymentMethodId;
    }

    public String getDescription() {
        return description;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }
}