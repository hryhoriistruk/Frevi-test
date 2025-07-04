package HireCraft.com.SpringBoot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // Responds with 400 Bad Request
public class InvalidBookingStatusTransitionException extends RuntimeException {
    public InvalidBookingStatusTransitionException(String message) {
        super(message);
    }
}