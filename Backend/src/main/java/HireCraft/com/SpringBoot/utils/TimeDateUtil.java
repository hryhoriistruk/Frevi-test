package HireCraft.com.SpringBoot.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeDateUtil {

    public static String getDateLabel(LocalDateTime dateTime) {
        LocalDate messageDate = dateTime.toLocalDate();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        if (messageDate.equals(today)) return "Today";
        if (messageDate.equals(yesterday)) return "Yesterday";

        return messageDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")); // e.g., June 10, 2025
    }

    public static String formatTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("h:mm a")); // e.g., 3:45 PM
    }
}
