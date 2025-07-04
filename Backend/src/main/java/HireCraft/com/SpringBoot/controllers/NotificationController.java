package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.response.NotificationResponse;
import HireCraft.com.SpringBoot.dtos.requests.NotificationRequest;
import HireCraft.com.SpringBoot.dtos.response.ReviewResponse;
import HireCraft.com.SpringBoot.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Get all notifications for current user
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_PROVIDER') or hasRole('ROLE_CLIENT')")
    public List<NotificationResponse> getUserNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        return notificationService.getUserNotifications(userDetails);
    }

    // Get notifications with pagination
    @GetMapping("/paginated")
    public ResponseEntity<Page<NotificationResponse>> getUserNotificationsPaginated(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = extractUserIdFromUserDetails(userDetails);
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(userId, page, size);
        return ResponseEntity.ok(notifications);
    }

    // Get unread notifications
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserIdFromUserDetails(userDetails);
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread/count")
    @PreAuthorize("hasRole('ROLE_PROVIDER') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<Long> getUnreadCount( @AuthenticationPrincipal UserDetails userDetails) {
        // Directly pass userDetails to the service, as it now handles extraction
        long count = notificationService.getUnreadCount(userDetails);
        return ResponseEntity.ok(count);
    }

    // Mark notification as read
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasRole('ROLE_PROVIDER') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Directly pass userDetails to the service, as it now handles extraction
        boolean updated = notificationService.markAsRead(notificationId, userDetails);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Mark all notifications as read
    @PutMapping("/read-all")
    public ResponseEntity<Integer> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserIdFromUserDetails(userDetails);
        int updatedCount = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(updatedCount);
    }

    // Delete notification
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = extractUserIdFromUserDetails(userDetails);
        boolean deleted = notificationService.deleteNotification(notificationId, userId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Create notification (for testing or admin purposes)
    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @RequestBody NotificationRequest request) {
        NotificationResponse notification = notificationService.createNotification(request);
        return ResponseEntity.ok(notification);
    }

    // Helper method to extract user ID from UserDetails
    // Adjust this based on your UserDetails implementation
    private Long extractUserIdFromUserDetails(UserDetails userDetails) {
        // This is a placeholder - implement based on your User/UserDetails structure
        // For example, if you have a custom UserDetails implementation:
        // return ((CustomUserDetails) userDetails).getUserId();

        // Or if you store ID in username:
        // return Long.parseLong(userDetails.getUsername());

        // For now, returning null - you need to implement this
        throw new RuntimeException("Implement extractUserIdFromUserDetails based on your UserDetails structure");
    }
}
