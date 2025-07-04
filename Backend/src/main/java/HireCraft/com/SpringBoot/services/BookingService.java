package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.BookingRequest;
import HireCraft.com.SpringBoot.dtos.requests.UpdateBookingStatusRequest;
import HireCraft.com.SpringBoot.dtos.response.BookingChartResponse;
import HireCraft.com.SpringBoot.dtos.response.BookingResponse;
import HireCraft.com.SpringBoot.dtos.response.ClientBookingViewResponse;
import HireCraft.com.SpringBoot.dtos.response.ProviderDashboardMetricsResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, UserDetails userDetails);
    List<BookingResponse> getBookingsForProvider(UserDetails userDetails);
    BookingResponse updateBookingStatus(Long bookingId, UpdateBookingStatusRequest request, UserDetails userDetails);
    List<ClientBookingViewResponse> getBookingsForClient(UserDetails userDetails);

    long getNewBookingRequestsCountToday(UserDetails userDetails);
    long getCompletedJobsCountForProvider(UserDetails userDetails);
    long getAcceptedJobsCountForProvider(UserDetails userDetails);
    long countCompletedJobsForProvider(Long providerId);
    ProviderDashboardMetricsResponse getProviderDashboardMetrics(UserDetails userDetails);

    List<BookingChartResponse> getMonthlyBookingChart(UserDetails userDetails);
    long getRejectedJobsCountForProvider(UserDetails userDetails);
}
