package oasis.task.tech.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:2:45PM
 * Project:task-management
 */

public class JsonResponse {
    @JsonProperty("status")
    private HttpStatus status;

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("data")
    private Object data;

    @JsonProperty("message")
    private String message;

    public JsonResponse(HttpStatus status, boolean success, Object data, String message) {
        this.status = status;
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public JsonResponse(HttpStatus status, boolean success, Object data) {
        this.status = status;
        this.success = success;
        this.data = data;
    }

    public JsonResponse(HttpStatus status, boolean success, String message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }

    public JsonResponse(HttpStatus status, Object data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }
}
