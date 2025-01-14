package javaweb.task_management_system.dtos;

import jakarta.validation.constraints.NotBlank;

public class AdminUserRequest {
    @NotBlank(message = "User is required!")
    private String user;
    private String role;
    @NotBlank(message = "Operation is required!")
    private String operation;

    public AdminUserRequest(String user, String role, String operation) {
        this.user = user;
        this.role = role;
        this.operation = operation;
    }



    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
