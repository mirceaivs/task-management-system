package javaweb.task_management_system.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException (String message) {
        super(message);
    }
}
