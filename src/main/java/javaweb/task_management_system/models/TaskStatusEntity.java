package javaweb.task_management_system.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "task_statuses")
public class TaskStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Task's status should have a name!")
    @Column(unique = true, nullable = false)
    private String name;

    public TaskStatusEntity() {
    }

    public TaskStatusEntity(String name) {
        this.name = name;
    }

    public TaskStatusEntity(Long id){
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
