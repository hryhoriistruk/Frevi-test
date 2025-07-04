package HireCraft.com.SpringBoot.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookingRequest {
    private Long providerId;
    private String timeSlot;
    private String estimatedDuration;
    private String description;

    public Long getProviderId() {
        return providerId;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getEstimatedDuration() {
        return estimatedDuration;
    }

    public String getDescription() {
        return description;
    }
}

