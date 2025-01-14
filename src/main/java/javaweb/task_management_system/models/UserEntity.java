package javaweb.task_management_system.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required!")
    private String name;

    @NotBlank(message = "Last Name is required!")
    @JsonProperty("lastname")
    private String lastName;

    @NotBlank(message = "Email is required!")
    @Column(nullable = false, unique = true)
    private String email;

    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectEntity> projects = new HashSet<>();

    @ManyToMany(mappedBy = "assignees")
    private Set<TaskEntity> tasks = new HashSet<>();

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<NotificationEntity> notifications = new HashSet<>();

    public UserEntity (Long id, String name, String lastName, String email) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
    }

    public UserEntity() {}

    public Set<TaskEntity> getTasks() {
        return tasks;
    }

    public Set<NotificationEntity> getNotifications() {
        return notifications;
    }

    public Long getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getLastname() {
        return lastName;
    }

    public String getPassword(){
        return password;
    }

    public Set<RoleEntity> getRoles(){
        return this.roles;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setTasks(Set<TaskEntity> tasks) {
        this.tasks = tasks;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
