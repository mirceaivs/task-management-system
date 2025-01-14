package javaweb.task_management_system.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
public class RoleEntity implements Comparable<RoleEntity>, GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    public RoleEntity() {}

    public RoleEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int compareTo(RoleEntity other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
