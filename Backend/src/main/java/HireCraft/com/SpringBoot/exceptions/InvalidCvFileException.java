package HireCraft.com.SpringBoot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // This will make Spring return a 400 Bad Request status
public class InvalidCvFileException extends RuntimeException {
    public InvalidCvFileException(String message) {
        super(message);
    }

    public InvalidCvFileException(String message, Throwable cause) {
        super(message, cause);
    }
}