package javaweb.task_management_system.dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDetailsDTO {
    private Long id;
    private String name;
    private String status;
    private List<String> assignedUsers = new ArrayList<>();
    private String projectName;
    private LocalDateTime dueDate;
    private String taskOwner;

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public String getTaskOwner() {
        return taskOwner;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getAssignedUsers() {
        return assignedUsers;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTaskOwner(String taskOwner) {
        this.taskOwner = taskOwner;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAssignedUsers(List<String> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addAssignedUser(String email) {
        this.assignedUsers.add(email);
    }
}
