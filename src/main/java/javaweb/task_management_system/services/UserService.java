package javaweb.task_management_system.services;


import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.UserDTO;
import javaweb.task_management_system.models.UserEntity;


public interface UserService {
    UserEntity findByEmail(String email);
    UserDTO convertToDto(UserEntity user);
    UserEntity convertToEntity(UserDTO user);
    ActionSuccessResponse deleteUser();
    ActionSuccessResponse updateUser(UserEntity updatedUser);
}

