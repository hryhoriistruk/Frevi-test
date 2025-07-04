package HireCraft.com.SpringBoot.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentBreakdown {
    private BigDecimal totalAmount;
    private BigDecimal platformFeePercentage;
    private BigDecimal platformFee;
    private BigDecimal providerAmount;

    public static PaymentBreakdown calculate(BigDecimal amount, BigDecimal feePercentage) {
        BigDecimal platformFee = amount.multiply(feePercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal providerAmount = amount.subtract(platformFee);

        return PaymentBreakdown.builder()
                .totalAmount(amount)
                .platformFeePercentage(feePercentage)
                .platformFee(platformFee)
                .providerAmount(providerAmount)
                .build();
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPlatformFeePercentage() {
        return platformFeePercentage;
    }

    public void setPlatformFeePercentage(BigDecimal platformFeePercentage) {
        this.platformFeePercentage = platformFeePercentage;
    }

    public BigDecimal getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(BigDecimal platformFee) {
        this.platformFee = platformFee;
    }

    public BigDecimal getProviderAmount() {
        return providerAmount;
    }

    public void setProviderAmount(BigDecimal providerAmount) {
        this.providerAmount = providerAmount;
    }
}
