// 1. NOTIFICATION MODEL/ENTITY
package HireCraft.com.SpringBoot.models;

import HireCraft.com.SpringBoot.enums.NotificationType;
import HireCraft.com.SpringBoot.enums.ReferenceType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private Long userId; // ID of the user who should receive this notification

    @Column(nullable = false)
    private Boolean isRead = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Long referenceId;

    @Enumerated(EnumType.STRING)
    private ReferenceType referenceType;

    public Notification(Long id, String message, NotificationType type, Long userId, Boolean isRead, LocalDateTime createdAt, Long referenceId, ReferenceType referenceType) {
        this.id = id;
        this.message = message;
        this.type = type;
        this.userId = userId;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }
}