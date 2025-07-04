package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.PaymentBreakdown;
import HireCraft.com.SpringBoot.dtos.requests.PaymentRequest;
import HireCraft.com.SpringBoot.dtos.response.PaymentResponse;
import HireCraft.com.SpringBoot.services.PaymentService;
import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/make-payment")
    @PreAuthorize("hasAuthority('MAKE_PAYMENT')")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        PaymentResponse response = paymentService.processPayment(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        PaymentResponse response = paymentService.getPaymentById(paymentId, userDetails);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/client")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<PaymentResponse>> getClientPayments(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<PaymentResponse> responses = paymentService.getClientPayments(userDetails);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/provider")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<List<PaymentResponse>> getProviderPayments(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<PaymentResponse> responses = paymentService.getProviderPayments(userDetails);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasRole('CLIENT') or hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getBookingPayments(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<PaymentResponse> responses = paymentService.getBookingPayments(bookingId, userDetails);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/breakdown")
    public ResponseEntity<PaymentBreakdown> calculatePaymentBreakdown(
            @RequestParam BigDecimal amount) {
        PaymentBreakdown breakdown = paymentService.calculatePaymentBreakdown(amount);
        return ResponseEntity.ok(breakdown);
    }

    @GetMapping("/provider/earnings")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<BigDecimal> getProviderEarnings(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @AuthenticationPrincipal UserDetails userDetails) {
        BigDecimal earnings = paymentService.calculateProviderEarnings(null, startDate, endDate, userDetails);
        return ResponseEntity.ok(earnings);
    }

    // Admin endpoints
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> processPaymentAdmin(
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/admin/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> getPaymentByIdAdmin(
            @PathVariable Long paymentId) {
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/client/{clientId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getClientPaymentsAdmin(
            @PathVariable Long clientId) {
        List<PaymentResponse> responses = paymentService.getClientPayments(clientId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/admin/provider/{providerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getProviderPaymentsAdmin(
            @PathVariable Long providerId) {
        List<PaymentResponse> responses = paymentService.getProviderPayments(providerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/admin/booking/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getBookingPaymentsAdmin(
            @PathVariable Long bookingId) {
        List<PaymentResponse> responses = paymentService.getBookingPayments(bookingId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/admin/provider/{providerId}/earnings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BigDecimal> getProviderEarningsAdmin(
            @PathVariable Long providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal earnings = paymentService.calculateProviderEarnings(providerId, startDate, endDate);
        return ResponseEntity.ok(earnings);
    }

    @GetMapping("/admin/platform-revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BigDecimal> getPlatformRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal revenue = paymentService.calculatePlatformRevenue(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }
}