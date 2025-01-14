package javaweb.task_management_system.dtos;

public class ActionSuccessResponse {
    private final String email;
    private final String status;

    public ActionSuccessResponse(String email, String status) {
        this.email = email;
        this.status = status;
    }

    public String getMessage(){
        return status;
    }

    public String getEmail() {
        return email;
    }
}
