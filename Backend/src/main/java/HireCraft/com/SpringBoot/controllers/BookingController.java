package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.requests.BookingRequest;
import HireCraft.com.SpringBoot.dtos.requests.UpdateBookingStatusRequest;
import HireCraft.com.SpringBoot.dtos.response.BookingChartResponse;
import HireCraft.com.SpringBoot.dtos.response.BookingResponse;
import HireCraft.com.SpringBoot.dtos.response.ClientBookingViewResponse;
import HireCraft.com.SpringBoot.dtos.response.ProviderDashboardMetricsResponse;
import HireCraft.com.SpringBoot.services.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
//@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('BOOK_SERVICE_PROVIDER')")
    public BookingResponse createBooking(@RequestBody BookingRequest request,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        return bookingService.createBooking(request, userDetails);
    }

    @GetMapping("/provider/me")
    @PreAuthorize("hasAuthority('VIEW_BOOKING_REQUEST_PROVIDER')")
    public List<BookingResponse> getProviderBookings(@AuthenticationPrincipal UserDetails userDetails) {
        return bookingService.getBookingsForProvider(userDetails);
    }

    @GetMapping("/client/me")
    @PreAuthorize("hasAuthority('VIEW_BOOKING_REQUEST_CLIENT')")
    public List<ClientBookingViewResponse> getClientBookings(@AuthenticationPrincipal UserDetails userDetails) {
        return bookingService.getBookingsForClient(userDetails);
    }


    @PatchMapping("/{bookingId}/status") // Use PATCH for partial update
    @PreAuthorize("hasAuthority('CANCEL_BOOKING_REQUEST') or hasAuthority('UPDATE_BOOKING_REQUEST_STATUS')")
    public ResponseEntity<BookingResponse> updateBookingStatus(@PathVariable Long bookingId,
                                                               @RequestBody @Valid UpdateBookingStatusRequest request,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        BookingResponse updatedBooking = bookingService.updateBookingStatus(bookingId, request, userDetails);
        return ResponseEntity.ok(updatedBooking);
    }

    @GetMapping("/provider/dashboard/metrics")
    @PreAuthorize("hasRole('ROLE_PROVIDER')") // Secure this endpoint
    public ResponseEntity<ProviderDashboardMetricsResponse> getProviderDashboardMetrics(@AuthenticationPrincipal UserDetails userDetails) {
        ProviderDashboardMetricsResponse metrics = bookingService.getProviderDashboardMetrics(userDetails);
        return ResponseEntity.ok(metrics);
    }

//    @GetMapping("/client/dashboard/metrics")
//    @PreAuthorize("hasRole('ROLE_CLIENT)") // Secure this endpoint
//    public ResponseEntity<ClientDashboardMetricsResponse> getClientDashboardMetrics(@AuthenticationPrincipal UserDetails userDetails) {
//        ProviderDashboardMetricsResponse metrics = bookingService.getClientDashboardMetrics(userDetails);
//        return ResponseEntity.ok(metrics);
//    }

    @GetMapping("/provider/chart/monthly")
    @PreAuthorize("hasRole('ROLE_PROVIDER')")
    public ResponseEntity<List<BookingChartResponse>> getMonthlyBookingChart(@AuthenticationPrincipal UserDetails userDetails) {
            List<BookingChartResponse> chartData = bookingService.getMonthlyBookingChart(userDetails);
            return ResponseEntity.ok(chartData);
    }
}
