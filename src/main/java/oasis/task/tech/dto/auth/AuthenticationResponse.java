package oasis.task.tech.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import oasis.task.tech.dto.security.RoleDto;

import java.io.Serializable;
import java.util.Set;
/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */
@Getter
@Setter
@NoArgsConstructor
public class AuthenticationResponse implements Serializable {
    private static final long serialVersionUID = 1250166508152483573L;

    private String token;

    private Long expiresIn;

    private String email;

    private String applicationId;

    private String fullName;

    private Set<RoleDto> roles;


    public AuthenticationResponse(String token, Long expiresIn, String email, String fullName, Set<RoleDto> roles) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
    }

    public AuthenticationResponse(String token, Long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

    @Override public String toString() { return token;}
}
