package HireCraft.com.SpringBoot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // Responds with 403 Forbidden
public class UnauthorizedBookingActionException extends RuntimeException {
    public UnauthorizedBookingActionException(String message) {
        super(message);
    }
}