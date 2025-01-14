package javaweb.task_management_system.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Projects should have a name!")
    private String name;

    @NotBlank(message = "The project need to have a description!")
    @Size(min=30, message = "The description should be longer than 30 words!")
    private String description;

    @ManyToOne
    @JoinColumn(name="owner_id", nullable = false)
    private UserEntity owner;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TaskEntity> tasks = new HashSet<>();

    public ProjectEntity(){}

    public ProjectEntity(String name, String description, UserEntity owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    public ProjectEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public UserEntity getOwner(){
        return this.owner;
    }

    public Set<TaskEntity> getTasks(){
        return this.tasks;
    }

    public void setOwner(UserEntity user) {
        this.owner = user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
