package HireCraft.com.SpringBoot.processor;

import HireCraft.com.SpringBoot.dtos.response.StripePaymentResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class StripePaymentProcessor {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public StripePaymentResponse processPayment(String paymentMethodId,
                                                BigDecimal totalAmount,
                                                BigDecimal platformFee,
                                                Long providerId) {
        try {
            // Convert to cents for Stripe
            long amountInCents = totalAmount.multiply(BigDecimal.valueOf(100)).longValue();
            long platformFeeInCents = platformFee.multiply(BigDecimal.valueOf(100)).longValue();

            // Create payment intent
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("usd")
                    .setPaymentMethod(paymentMethodId)
                    .setConfirm(true)
                    .setApplicationFeeAmount(platformFeeInCents)
                    .putMetadata("provider_id", providerId.toString())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            log.info("Stripe payment intent created: {}", intent.getId());

            return new StripePaymentResponse(true, intent.getId(), intent.getStatus());

        } catch (StripeException e) {
            log.error("Stripe payment failed", e);
            return new StripePaymentResponse(false, null, e.getMessage());
        }
    }
}
