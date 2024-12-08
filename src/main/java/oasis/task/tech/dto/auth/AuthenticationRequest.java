package oasis.task.tech.dto.auth;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */
@Getter
@Setter
public class AuthenticationRequest implements Serializable {
    private static final long serialVersionUID = -8445943548965154778L;

    @NotNull
    private String username;

    @NotNull
    private String password;

    private Boolean rememberMe;

    public AuthenticationRequest() {
    }

    public AuthenticationRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    @Override
    public String toString() {
        return "AuthenticationRequest {" +
                "username='" + username + '\'' +
                ", rememberMe=" + rememberMe +
                '}';
    }

    public Boolean isRememberMe() {
        return rememberMe;
    }
}
