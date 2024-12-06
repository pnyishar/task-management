package oasis.task.tech.domains.actors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import oasis.task.tech.constants.UserType;
import oasis.task.tech.domains.base.StringIdentifierModel;
import oasis.task.tech.domains.security.Permission;
import oasis.task.tech.domains.security.Role;
import oasis.task.tech.util.WebUtility;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */
@Entity
@Inheritance( strategy = InheritanceType.JOINED)
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted_at is null")
@ToString
@AllArgsConstructor
@Table(name = "users")
public class User extends StringIdentifierModel implements UserDetails {
    @Size(min = 5)
    private String fullName;

    @Pattern(regexp = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    @Column(unique = true)
    private String email;

    @NotNull
    private String password;

    private String phone;

    private boolean activated;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoggedIn;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Permission> extraPermissions;

    public User() {
        extraPermissions = new HashSet<>();
    }

    public User(String email, String password) {
        this();
        this.email = email;
        this.password = password;
    }

    public User(String email, String password, UserType userType) {
        this(email, password);
        this.userType = userType;
    }


    public Set<Permission> getPermissions() {
        Set<Permission> permissions = getRolePermissions();
        permissions.addAll(extraPermissions);
        return permissions;
    }

    public boolean hasPermission(String permission){
        return getPermissions().contains(new Permission(permission));
    }

    @JsonIgnore
    private Set<Permission> getRolePermissions() {
        return roles == null
                ? new HashSet<>()
                : roles.stream().map(Role::getPermissions).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    @Override
    public void prePersist() {
        if (StringUtils.isBlank(this.email)) this.email = null;
        if (StringUtils.isBlank(this.password)) this.password = "12345";
        this.hashPassword();
    }

    public void preUpdate() {
        this.hashPassword();
    }

    private void hashPassword(){
        if (!StringUtils.isBlank(this.password) && !WebUtility.isBcryptHashed(this.password)) {
            this.setPassword(WebUtility.hashPassword(this.password));
        }
    }

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void activate(){
        this.activated = true;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
