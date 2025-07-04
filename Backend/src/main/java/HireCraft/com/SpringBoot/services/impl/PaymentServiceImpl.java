//package HireCraft.com.SpringBoot.services.impl;
//
//import HireCraft.com.SpringBoot.dtos.PaymentBreakdown;
//import HireCraft.com.SpringBoot.dtos.requests.PaymentRequest;
//import HireCraft.com.SpringBoot.dtos.response.PaymentResponse;
//import HireCraft.com.SpringBoot.dtos.response.StripePaymentResponse;
//import HireCraft.com.SpringBoot.enums.PaymentStatus;
//import HireCraft.com.SpringBoot.exceptions.PaymentNotFoundException;
//import HireCraft.com.SpringBoot.exceptions.PaymentProcessingException;
//import HireCraft.com.SpringBoot.models.Payment;
//import HireCraft.com.SpringBoot.models.User;
//import HireCraft.com.SpringBoot.processor.StripePaymentProcessor;
//import HireCraft.com.SpringBoot.repository.PaymentRepository;
//import HireCraft.com.SpringBoot.repository.UserRepository;
//import HireCraft.com.SpringBoot.services.PaymentService;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.transaction.annotation.Transactional;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//@Slf4j
//public class PaymentServiceImpl implements PaymentService {
//
//    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
//    private final PaymentRepository paymentRepository;
//    private final StripePaymentProcessor stripePaymentProcessor;
//    private final UserRepository userRepository;
//
//    @Value("${hirecraft.payment.platform-fee-percentage}")
//    private BigDecimal platformFeePercentage;
//
//    public PaymentServiceImpl(PaymentRepository paymentRepository,
//                              StripePaymentProcessor stripePaymentProcessor, UserRepository userRepository) {
//        this.paymentRepository = paymentRepository;
//        this.stripePaymentProcessor = stripePaymentProcessor;
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public PaymentResponse processPayment(PaymentRequest request, UserDetails userDetails) {
//        log.info("Processing payment with UserDetails: {}", userDetails.getUsername());
//
//        // 🧠 Find user in your database based on email or username
//        User user = userRepository.findByEmail(userDetails.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername()));
//
//        if (user.getRole().name().equals("ROLE_CLIENT")) {
//            request.setClientId(user.getId());
//        } else if (user.getRole().name().equals("ROLE_PROVIDER")) {
//            request.setProviderId(user.getId());
//        }
//
//        return processPayment(request);
//    }
//
//
//    @Override
//    public PaymentResponse processPayment(PaymentRequest request) {
//        log.info("Processing payment for client: {} to provider: {}",
//                request.getClientId(), request.getProviderId());
//
//        try {
//            // Calculate payment breakdown
//            PaymentBreakdown breakdown = calculatePaymentBreakdown(request.getAmount());
//
//            // Create payment record
//            Payment payment = Payment.builder()
//                    .clientId(request.getClientId())
//                    .providerId(request.getProviderId())
//                    .bookingId(request.getBookingId())
//                    .totalAmount(breakdown.getTotalAmount())
//                    .platformFeePercentage(breakdown.getPlatformFeePercentage())
//                    .platformFee(breakdown.getPlatformFee())
//                    .providerAmount(breakdown.getProviderAmount())
//                    .status(PaymentStatus.PENDING)
//                    .paymentMethod(request.getPaymentMethod())
//                    .description(request.getDescription())
//                    .build();
//
//            // Save payment
//            payment = paymentRepository.save(payment);
//
//            // Process with payment processor
//            StripePaymentResponse stripeResponse = stripePaymentProcessor.processPayment(
//                    request.getPaymentMethodId(),
//                    breakdown.getTotalAmount(),
//                    breakdown.getPlatformFee(),
//                    request.getProviderId()
//            );
//
//            // Update payment with result
//            payment.setExternalTransactionId(stripeResponse.getTransactionId());
//            payment.setStatus(stripeResponse.isSuccess() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
//            payment = paymentRepository.save(payment);
//
//            log.info("Payment processed successfully. Payment ID: {}, Status: {}",
//                    payment.getId(), payment.getStatus());
//
//            return PaymentResponse.fromEntity(payment);
//
//        } catch (Exception e) {
//            log.error("Failed to process payment", e);
//            throw new PaymentProcessingException("Failed to process payment: " + e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public PaymentResponse getPaymentById(Long paymentId) {
//        Payment payment = paymentRepository.findById(paymentId)
//                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
//        return PaymentResponse.fromEntity(payment);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<PaymentResponse> getClientPayments(Long clientId) {
//        return paymentRepository.findByClientIdOrderByCreatedAtDesc(clientId)
//                .stream()
//                .map(PaymentResponse::fromEntity)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<PaymentResponse> getProviderPayments(Long providerId) {
//        return paymentRepository.findByProviderIdOrderByCreatedAtDesc(providerId)
//                .stream()
//                .map(PaymentResponse::fromEntity)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public PaymentBreakdown calculatePaymentBreakdown(BigDecimal amount) {
//        return PaymentBreakdown.calculate(amount, platformFeePercentage);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public BigDecimal calculateProviderEarnings(Long providerId, LocalDateTime startDate, LocalDateTime endDate) {
//        BigDecimal earnings = paymentRepository.calculateProviderEarnings(providerId, startDate, endDate);
//        return earnings != null ? earnings : BigDecimal.ZERO;
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public BigDecimal calculatePlatformRevenue(LocalDateTime startDate, LocalDateTime endDate) {
//        BigDecimal revenue = paymentRepository.calculateTotalPlatformRevenue(startDate, endDate);
//        return revenue != null ? revenue : BigDecimal.ZERO;
//    }
//
//    // In PaymentService interface and implementation
//    @Override
//    @Transactional(readOnly = true)
//    public List<PaymentResponse> getBookingPayments(Long bookingId) {
//        return paymentRepository.findByBookingIdOrderByCreatedAtDesc(bookingId)
//                .stream()
//                .map(PaymentResponse::fromEntity)
//                .collect(Collectors.toList());
//    }
//}

package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.PaymentBreakdown;
import HireCraft.com.SpringBoot.dtos.requests.PaymentRequest;
import HireCraft.com.SpringBoot.dtos.response.PaymentResponse;
import HireCraft.com.SpringBoot.dtos.response.StripePaymentResponse;
import HireCraft.com.SpringBoot.enums.PaymentStatus;
import HireCraft.com.SpringBoot.exceptions.PaymentNotFoundException;
import HireCraft.com.SpringBoot.exceptions.PaymentProcessingException;
import HireCraft.com.SpringBoot.models.Payment;
import HireCraft.com.SpringBoot.models.User;
import HireCraft.com.SpringBoot.models.ClientProfile;
import HireCraft.com.SpringBoot.models.ServiceProviderProfile;
import HireCraft.com.SpringBoot.processor.StripePaymentProcessor;
import HireCraft.com.SpringBoot.repository.PaymentRepository;
import HireCraft.com.SpringBoot.repository.UserRepository;
import HireCraft.com.SpringBoot.repository.ClientProfileRepository;
import HireCraft.com.SpringBoot.repository.ServiceProviderProfileRepository;
import HireCraft.com.SpringBoot.services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final StripePaymentProcessor stripePaymentProcessor;
    private final UserRepository userRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final ServiceProviderProfileRepository serviceProviderProfileRepository;

    @Value("${hirecraft.payment.platform-fee-percentage}")
    private BigDecimal platformFeePercentage;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              StripePaymentProcessor stripePaymentProcessor,
                              UserRepository userRepository,
                              ClientProfileRepository clientProfileRepository,
                              ServiceProviderProfileRepository serviceProviderProfileRepository) {
        this.paymentRepository = paymentRepository;
        this.stripePaymentProcessor = stripePaymentProcessor;
        this.userRepository = userRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.serviceProviderProfileRepository = serviceProviderProfileRepository;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request, UserDetails userDetails) {
        // Get authenticated user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get client profile from user email
        ClientProfile clientProfile = clientProfileRepository.findByUserEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        log.info("Processing payment with authenticated user: {}", user.getEmail());

        try {
            // Calculate payment breakdown
            PaymentBreakdown breakdown = calculatePaymentBreakdown(request.getAmount());

            // Create payment record
            Payment payment = Payment.builder()
                    .clientId(clientProfile.getUser().getId())
                    .providerId(request.getProviderId())
                    .bookingId(request.getBookingId())
                    .totalAmount(breakdown.getTotalAmount())
                    .platformFeePercentage(breakdown.getPlatformFeePercentage())
                    .platformFee(breakdown.getPlatformFee())
                    .providerAmount(breakdown.getProviderAmount())
                    .status(PaymentStatus.PENDING)
                    .paymentMethod(request.getPaymentMethod())
                    .description(request.getDescription())
                    .build();

            // Save payment
            payment = paymentRepository.save(payment);

            // Process with payment processor
            StripePaymentResponse stripeResponse = stripePaymentProcessor.processPayment(
                    request.getPaymentMethodId(),
                    breakdown.getTotalAmount(),
                    breakdown.getPlatformFee(),
                    request.getProviderId()
            );

            // Update payment with result
            payment.setExternalTransactionId(stripeResponse.getTransactionId());
            payment.setStatus(stripeResponse.isSuccess() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
            payment = paymentRepository.save(payment);

            log.info("Payment processed successfully. Payment ID: {}, Status: {}",
                    payment.getId(), payment.getStatus());

            return PaymentResponse.fromEntity(payment);

        } catch (Exception e) {
            log.error("Failed to process payment", e);
            throw new PaymentProcessingException("Failed to process payment: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId, UserDetails userDetails) {
        // Get authenticated user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));

        return PaymentResponse.fromEntity(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getClientPayments(UserDetails userDetails) {
        // Get authenticated user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get client profile from user email
        ClientProfile clientProfile = clientProfileRepository.findByUserEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        return paymentRepository.findByClientIdOrderByCreatedAtDesc(clientProfile.getUser().getId())
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getProviderPayments(UserDetails userDetails) {
        // Get authenticated user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get service provider profile from user email
        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findByUserEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        return paymentRepository.findByProviderIdOrderByCreatedAtDesc(providerProfile.getUser().getId())
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getBookingPayments(Long bookingId, UserDetails userDetails) {
        // Get authenticated user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return paymentRepository.findByBookingIdOrderByCreatedAtDesc(bookingId)
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentBreakdown calculatePaymentBreakdown(BigDecimal amount) {
        return PaymentBreakdown.calculate(amount, platformFeePercentage);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateProviderEarnings(Long providerId, LocalDateTime startDate,
                                                LocalDateTime endDate, UserDetails userDetails) {
        // Get authenticated user
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get service provider profile from user email
        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findByUserEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        BigDecimal earnings = paymentRepository.calculateProviderEarnings(providerProfile.getUser().getId(), startDate, endDate);
        return earnings != null ? earnings : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculatePlatformRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal revenue = paymentRepository.calculateTotalPlatformRevenue(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    // Non-UserDetails methods for backward compatibility or admin access
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            PaymentBreakdown breakdown = calculatePaymentBreakdown(request.getAmount());

            Payment payment = Payment.builder()
                    .clientId(request.getClientId())
                    .providerId(request.getProviderId())
                    .bookingId(request.getBookingId())
                    .totalAmount(breakdown.getTotalAmount())
                    .platformFeePercentage(breakdown.getPlatformFeePercentage())
                    .platformFee(breakdown.getPlatformFee())
                    .providerAmount(breakdown.getProviderAmount())
                    .status(PaymentStatus.PENDING)
                    .paymentMethod(request.getPaymentMethod())
                    .description(request.getDescription())
                    .build();

            payment = paymentRepository.save(payment);

            StripePaymentResponse stripeResponse = stripePaymentProcessor.processPayment(
                    request.getPaymentMethodId(),
                    breakdown.getTotalAmount(),
                    breakdown.getPlatformFee(),
                    request.getProviderId()
            );

            payment.setExternalTransactionId(stripeResponse.getTransactionId());
            payment.setStatus(stripeResponse.isSuccess() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
            payment = paymentRepository.save(payment);

            return PaymentResponse.fromEntity(payment);

        } catch (Exception e) {
            log.error("Failed to process payment", e);
            throw new PaymentProcessingException("Failed to process payment: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));
        return PaymentResponse.fromEntity(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getClientPayments(Long clientId) {
        return paymentRepository.findByClientIdOrderByCreatedAtDesc(clientId)
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getProviderPayments(Long providerId) {
        return paymentRepository.findByProviderIdOrderByCreatedAtDesc(providerId)
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getBookingPayments(Long bookingId) {
        return paymentRepository.findByBookingIdOrderByCreatedAtDesc(bookingId)
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateProviderEarnings(Long providerId, LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal earnings = paymentRepository.calculateProviderEarnings(providerId, startDate, endDate);
        return earnings != null ? earnings : BigDecimal.ZERO;
    }
}