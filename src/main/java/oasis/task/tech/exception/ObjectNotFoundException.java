
package oasis.task.tech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:1:44PM
 * Project:task-management
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "The requested entity can not be found")
public class ObjectNotFoundException extends RuntimeException{

    public ObjectNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public ObjectNotFoundException(String msg) {
        super(msg);
    }

    public ObjectNotFoundException(String format, Object... args) {
        super(String.format(format, args));
    }
}
