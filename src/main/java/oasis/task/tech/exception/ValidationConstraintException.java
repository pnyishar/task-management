package oasis.task.tech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Some constraints are violated ...")
public class ValidationConstraintException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ValidationConstraintException(String message) {
        super(message);
    }
}
