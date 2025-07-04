package HireCraft.com.SpringBoot.dtos.response;

import HireCraft.com.SpringBoot.enums.NotificationType;
import HireCraft.com.SpringBoot.enums.ReferenceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String message;
    private NotificationType type;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private String timeAgo;
    private Long referenceId;
    private ReferenceType referenceType;
}