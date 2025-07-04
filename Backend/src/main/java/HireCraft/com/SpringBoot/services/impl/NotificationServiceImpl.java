package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.NotificationRequest;
import HireCraft.com.SpringBoot.dtos.response.NotificationResponse;
import HireCraft.com.SpringBoot.enums.NotificationType;
import HireCraft.com.SpringBoot.models.Notification;
import HireCraft.com.SpringBoot.models.User;
import HireCraft.com.SpringBoot.repository.NotificationRepository;
import HireCraft.com.SpringBoot.repository.UserRepository;
import HireCraft.com.SpringBoot.services.NotificationService; // Import the interface
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link NotificationService} interface.
 * Handles the business logic for notification management, including
 * creation, retrieval, updating read status, and deletion.
 */
@Service // Marks this class as a Spring Service component
@Slf4j // Lombok annotation for logging
public class NotificationServiceImpl implements NotificationService { // Implements the interface

    // The NotificationRepository is injected via Lombok's @RequiredArgsConstructor
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new notification in the database.
     *
     * @param request The NotificationRequest containing details for the notification.
     * @return A NotificationResponse DTO of the created notification.
     */
    @Override // Indicates that this method implements a method from the interface
    @Transactional // Ensures atomicity for database operations
    public NotificationResponse createNotification(NotificationRequest request) {
        // Build the Notification entity from the request DTO
        Notification notification = Notification.builder()
                .message(request.getMessage())
                .type(request.getType())
                .userId(request.getUserId())
                .referenceId(request.getReferenceId())
                .referenceType(request.getReferenceType())
                .isRead(false) // New notifications are initially unread
                .build();
        notification.setCreatedAt(LocalDateTime.now());
        // Save the notification to the repository
        Notification saved = notificationRepository.save(notification);

        // Convert the saved entity back to a DTO for response
        return convertToDto(saved);
    }

//    @Override
//    public List<NotificationResponse> getUserNotifications(UserDetails userDetails) {
//        // Fetch notifications from the repository
//        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
//        // Convert entities to DTOs and collect into a list
//        return notifications.stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }

    @Override
    public List<NotificationResponse> getUserNotifications(UserDetails userDetails) {
        // Use userDetails.getUsername() (which is typically the email or unique login)
        // to find your User entity (or ServiceProviderProfile, ClientProfile, etc.)
        // This mirrors the logic you used in your ReviewService.

        // Assuming User entity has an 'email' field and a 'findByEmail' method in UserRepository
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userDetails.getUsername()));

        Long userId = user.getId(); // Get the actual Long ID from your User entity

        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves notifications for a user with pagination.
     *
     * @param userId The ID of the user.
     * @param page The page number (0-indexed).
     * @param size The number of items per page.
     * @return A Page of NotificationResponse DTOs.
     */
    @Override
    public Page<NotificationResponse> getUserNotifications(Long userId, int page, int size) {
        // Create a Pageable object for pagination
        Pageable pageable = PageRequest.of(page, size);
        // Fetch paginated notifications
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        // Map the Page of entities to a Page of DTOs
        return notifications.map(this::convertToDto);
    }

    /**
     * Retrieves all unread notifications for a given user, ordered by creation time descending.
     *
     * @param userId The ID of the user.
     * @return A list of NotificationResponse DTOs.
     */
    @Override
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        // Fetch unread notifications
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        // Convert entities to DTOs
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public long getUnreadCount(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userDetails.getUsername()));

        Long userId = user.getId();
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * Marks a single notification as read.
     * Now accepts UserDetails and extracts userId internally.
     *
     * @param notificationId The ID of the notification to mark.
     * @param userDetails The UserDetails object of the authenticated user.
     * @return true if the notification was updated, false otherwise.
     */
    @Override
    @Transactional
    public boolean markAsRead(Long notificationId, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userDetails.getUsername()));

        Long userId = user.getId();
        // Execute the custom update query in the repository
        int updated = notificationRepository.markAsRead(notificationId, userId);
        return updated > 0; // Return true if at least one record was updated
    }

    /**
     * Marks all notifications for a given user as read.
     *
     * @param userId The ID of the user.
     * @return The number of notifications that were marked as read.
     */
    @Override
    @Transactional
    public int markAllAsRead(Long userId) {
        // Execute the custom update query
        return notificationRepository.markAllAsReadForUser(userId);
    }

    /**
     * Deletes a specific notification. Ensures the notification belongs to the specified user.
     *
     * @param notificationId The ID of the notification to delete.
     * @param userId The ID of the user to whom the notification belongs.
     * @return true if the notification was deleted, false otherwise.
     */
    @Override
    @Transactional
    public boolean deleteNotification(Long notificationId, Long userId) {
        return notificationRepository.findById(notificationId) // Find the notification by ID
                .filter(notification -> notification.getUserId().equals(userId)) // Ensure it belongs to the user
                .map(notification -> { // If found and user matches
                    notificationRepository.delete(notification); // Delete it
                    return true;
                })
                .orElse(false); // If not found or user does not match
    }

    /**
     * Utility method to create a notification for a new message received.
     *
     * @param userId The recipient's user ID.
     * @param senderName The name of the message sender.
     * @return The response DTO of the created notification.
     */
    @Override
    public NotificationResponse createMessageNotification(Long userId, String senderName) {
        String message = String.format("New message from %s", senderName);
        NotificationRequest request = NotificationRequest.builder()
                .message(message)
                .type(NotificationType.MESSAGE)
                .userId(userId)
                .build();
        return createNotification(request);
    }

    /**
     * Utility method to create a booking reminder notification.
     *
     * @param userId The recipient's user ID.
     * @param appointmentTime The time of the appointment.
     * @return The response DTO of the created notification.
     */
    @Override
    public NotificationResponse createBookingReminderNotification(Long userId, String appointmentTime) {
        String message = String.format("Booking reminder: %s appointment", appointmentTime);
        NotificationRequest request = NotificationRequest.builder()
                .message(message)
                .type(NotificationType.BOOKING_REMINDER)
                .userId(userId)
                .build();
        return createNotification(request);
    }

    /**
     * Utility method to create a payment received notification.
     *
     * @param userId The recipient's user ID.
     * @param amount The amount of payment received.
     * @return The response DTO of the created notification.
     */
    @Override
    public NotificationResponse createPaymentReceivedNotification(Long userId, String amount) {
        String message = String.format("Payment of %s received", amount);
        NotificationRequest request = NotificationRequest.builder()
                .message(message)
                .type(NotificationType.PAYMENT_RECEIVED)
                .userId(userId)
                .build();
        return createNotification(request);
    }

    /**
     * Utility method to create a review received notification.
     *
     * @param userId The recipient's user ID.
     * @param stars The number of stars given in the review.
     * @return The response DTO of the created notification.
     */
    @Override
    public NotificationResponse createReviewReceivedNotification(Long userId, int stars) {
        String message = String.format("New %d-star review received", stars);
        NotificationRequest request = NotificationRequest.builder()
                .message(message)
                .type(NotificationType.REVIEW_RECEIVED)
                .userId(userId)
                .build();
        return createNotification(request);
    }

    /**
     * Converts a Notification entity to its corresponding NotificationResponse DTO.
     * This is a private helper method as it's internal to the service logic.
     *
     * @param notification The Notification entity to convert.
     * @return The NotificationResponse DTO.
     */
    private NotificationResponse convertToDto(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .timeAgo(calculateTimeAgo(notification.getCreatedAt())) // Calculate human-readable time
                .referenceId(notification.getReferenceId())
                .referenceType(notification.getReferenceType())
                .build();
    }

    /**
     * Calculates a human-readable "time ago" string from a LocalDateTime.
     * This is a private helper method as it's internal to the service logic.
     *
     * @param createdAt The LocalDateTime to calculate the difference from.
     * @return A string representing the time difference (e.g., "5 min ago", "2 days ago").
     */
    private String calculateTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();

        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " min ago";

        long hours = ChronoUnit.HOURS.between(createdAt, now);
        // Using "hour" or "hours" based on quantity
        if (hours < 24) return hours + " hour" + (hours == 1 ? "" : "s") + " ago";

        long days = ChronoUnit.DAYS.between(createdAt, now);
        // Using "day" or "days" based on quantity
        if (days < 7) return days + " day" + (days == 1 ? "" : "s") + " ago";

        long weeks = days / 7;
        // Using "week" or "weeks" based on quantity
        if (weeks < 4) return weeks + " week" + (weeks == 1 ? "" : "s") + " ago";

        long months = ChronoUnit.MONTHS.between(createdAt, now);
        // Using "month" or "months" based on quantity
        if (months < 12) return months + " month" + (months == 1 ? "" : "s") + " ago";

        long years = ChronoUnit.YEARS.between(createdAt, now);
        // Using "year" or "years" based on quantity
        return years + " year" + (years == 1 ? "" : "s") + " ago";
    }

    /**
     * Deletes notifications older than a specified number of days.
     * This method is transactional as it modifies the database.
     *
     * @param daysToKeep The number of days to keep notifications.
     * @return The count of deleted notifications.
     */
    @Override
    @Transactional
    public int cleanupOldNotifications(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        return notificationRepository.deleteOldNotifications(cutoffDate);
    }
}
