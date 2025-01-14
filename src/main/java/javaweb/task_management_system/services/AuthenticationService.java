package javaweb.task_management_system.services;

import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.LoginResponseDTO;
import javaweb.task_management_system.dtos.UserDTO;
import javaweb.task_management_system.models.UserEntity;



public interface AuthenticationService {
    UserDTO register(UserEntity user, int roleNumber);
    LoginResponseDTO login(String email, String password);
    ActionSuccessResponse logout();
}
