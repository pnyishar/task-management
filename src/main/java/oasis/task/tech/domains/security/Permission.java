/**
 * Created By: Paul Nyishar
 * Date: 9/14/2024
 * Time: 9:41 PM
 * Project: hall-mgt-backend
 */
package oasis.task.tech.domains.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import oasis.task.tech.domains.base.NamedModel;
import org.hibernate.annotations.Where;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "permissions")
@NoArgsConstructor
@Where(clause = "deleted_at is null")
public class Permission extends NamedModel {

    @Lob
    @Column(name = "description")
    private String description;

    private boolean active;

    public Permission(String name){
        this.setName(name);
    }

    @Override
    public void setName(String name) {
        this.name = name.startsWith("ROLE_") ? name : "ROLE_" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Permission)) return false;
        Permission that = (Permission) o;
        return this == o
                || Objects.equals(this.getId(), that.getId())
                || (Objects.equals(active, that.active) && Objects.equals(getName(), that.getName()));
    }


    @Override
    public int hashCode() {
        return Objects.hash(getName(), active);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "name:" + this.getName() + "id:" + this.getId();
    }
}
