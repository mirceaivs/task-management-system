package javaweb.task_management_system.services;



import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.UserDTO;

import javax.swing.*;
import java.util.List;

public interface AdminServices {
    ActionSuccessResponse deleteUserByEmail(String email);
    ActionSuccessResponse grantAdminRole(String email);
    ActionSuccessResponse revokeAdminRole(String email);

}
