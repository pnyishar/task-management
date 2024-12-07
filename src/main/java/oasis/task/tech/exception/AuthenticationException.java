
package oasis.task.tech.exception;

import org.springframework.http.HttpStatus;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:1:44PM
 * Project:task-management
 */
public class AuthenticationException extends RuntimeException{
    private HttpStatus status;
    private String message;

    public AuthenticationException(HttpStatus status, String message){
        this.status = status;
        this.message = message;
    }

    public AuthenticationException(String s, HttpStatus status, String message){
        super(s);
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
