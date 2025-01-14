package javaweb.task_management_system.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name="tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tasks should have a name!")
    private String name;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private TaskStatusEntity status;

    @Column(name = "due_date")
    @FutureOrPresent(message = "The due date must be in the future or today")
    private LocalDateTime dueDate;

    @ManyToMany
    @JoinTable(
            name = "task_assignees",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> assignees = new HashSet<>();

    public TaskEntity(){}

    public TaskEntity(String name, ProjectEntity project, TaskStatusEntity status, LocalDateTime dueDate) {
        this.name = name;
        this.project = project;
        this.status = status;
        this.dueDate = dueDate;
    }

    public TaskEntity(String name) {
        this.name = name;
    }

    public Set<UserEntity> getAssignees() {
        return assignees;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAssignee(Set<UserEntity> assignees) {
        this.assignees = assignees;
    }

    public void setStatus(TaskStatusEntity status) {
        this.status = status;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public String getName() {
        return this.name;
    }

    public Long getId() {
        return id;
    }

    public ProjectEntity getProject() {
        return this.project;
    }

    public TaskStatusEntity getStatus() {
        return this.status;
    }


}
