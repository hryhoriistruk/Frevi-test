package HireCraft.com.SpringBoot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    /**
     * Construct a new UserNotFoundException with a custom message.
     *
     * @param message human-readable error message
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * Construct a new UserNotFoundException with a default message.
     */
    public UserNotFoundException() {
        super("User not found");
    }
}
