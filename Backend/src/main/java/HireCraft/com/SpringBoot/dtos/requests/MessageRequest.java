package HireCraft.com.SpringBoot.dtos.requests;

import lombok.Data;

@Data
public class MessageRequest {
    private Long bookingId;
    private String content; // Raw content (to be encrypted)

    public Long getBookingId() {
        return bookingId;
    }

    public String getContent() {
        return content;
    }
}
