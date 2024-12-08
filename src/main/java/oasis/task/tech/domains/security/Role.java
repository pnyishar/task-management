
package oasis.task.tech.domains.security;

import lombok.Getter;
import lombok.Setter;
import oasis.task.tech.domains.base.Model;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="roles")
@Where(clause = "deleted_at is null")
public class Role extends Model {
    @NotNull(message = "Name Cannot be null")
    @Column(unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permissions", joinColumns = {
            @JoinColumn(name = "role_id") },
            inverseJoinColumns = { @JoinColumn(name = "permission_id") })
    private Set<Permission> permissions = new HashSet<>();

    @Lob
    @Column(name="description")
    private String description;

    private boolean active;

    public Role(){

    }

    public Role(String name){
        this.name = name;
    }

    public Role addPermission(Permission permssion){
        permissions.add(permssion);
        return this;
    }

    @Override
    public String toString() {
        return "Role{" + "id=" + this.getId() + "name=" + name + ", description=" + description + ", active=" + active + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role that = (Role) o;
        return Objects.equals(active, that.active) &&
                Objects.equals(name, that.name) && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, active);
    }
}
