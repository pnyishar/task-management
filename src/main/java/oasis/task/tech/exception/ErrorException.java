package oasis.task.tech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:1:44PM
 * Project:task-management
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ErrorException extends RuntimeException{
    public ErrorException(String message) {
        super(message);
    }
}
