package HireCraft.com.SpringBoot.dtos.requests;

import HireCraft.com.SpringBoot.enums.NotificationType;
import HireCraft.com.SpringBoot.enums.ReferenceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest  {
    private String message;
    private NotificationType type;
    private Long userId;
    private Long referenceId;
    private ReferenceType referenceType;


    public String getMessage() {
        return message;
    }

    public NotificationType getType() {
        return type;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }
}