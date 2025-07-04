package HireCraft.com.SpringBoot.dtos.requests;

import HireCraft.com.SpringBoot.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingStatusRequest {
    @NotNull(message = "New status cannot be null")
    private BookingStatus newStatus;

    public @NotNull(message = "New status cannot be null") BookingStatus getNewStatus() {
        return newStatus;
    }
}