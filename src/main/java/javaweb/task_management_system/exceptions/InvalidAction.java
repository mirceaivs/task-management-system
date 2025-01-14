package javaweb.task_management_system.exceptions;

public class InvalidAction extends RuntimeException{
    public InvalidAction(String message){
        super(message);
    }
}
