package HireCraft.com.SpringBoot.dtos.response;

import lombok.Data;

@Data
public class MessageResponse {
    private String senderType;         // "CLIENT" or "PROVIDER"
    private String senderFullName;     // From either ClientProfile or ServiceProviderProfile
    private String content;            // Decrypted message content
    private String dateStamp;          // e.g., "Today", "Yesterday"
    private String timeSent;           // e.g., "3:45 PM"

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public void setSenderFullName(String senderFullName) {
        this.senderFullName = senderFullName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDateStamp(String dateStamp) {
        this.dateStamp = dateStamp;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }
}
