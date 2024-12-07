package oasis.task.tech.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:1:44PM
 * Project:task-management
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private boolean success = false;
    private int status;
    private String message;
    private Object data;

}
