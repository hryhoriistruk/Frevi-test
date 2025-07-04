package HireCraft.com.SpringBoot.dtos.response;

// package HireCraft.com.SpringBoot.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderDashboardMetricsResponse {
    private long newBookingRequestsToday;
    private long completedJobs;
    private long acceptedJobs;
    private long rejectedJobs;
    private long totalReviews;
    private double averageRating;
    private double dailyEarnings; // Placeholder for now, you'll need to implement this calculation
    private long unreadMessages;  // Placeholder for now, you'll need to implement this calculation

    public ProviderDashboardMetricsResponse(long completedJobs) {
        this.completedJobs = completedJobs;
    }

    public long getCompletedJobs() {
        return completedJobs;
    }
}
