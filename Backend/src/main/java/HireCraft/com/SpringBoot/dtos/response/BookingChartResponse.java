package HireCraft.com.SpringBoot.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingChartResponse {
    private String month;           // Day name: "Mon", "Tue", etc.
    private String fullDate;       // Full date: "Jan 15", "Jan 16", etc.
    private long acceptedBookings;
    private long completedBookings;
    private long rejectedBookings;
}