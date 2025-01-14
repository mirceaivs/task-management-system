package javaweb.task_management_system.services;


import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.UserDTO;
import javaweb.task_management_system.exceptions.InvalidAction;
import javaweb.task_management_system.exceptions.InvalidUserException;
import javaweb.task_management_system.exceptions.InvalidValueException;
import javaweb.task_management_system.exceptions.ResourceNotFoundException;
import javaweb.task_management_system.models.RoleEntity;
import javaweb.task_management_system.models.UserEntity;
import javaweb.task_management_system.repositories.RoleRepository;
import javaweb.task_management_system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminServicesImpl implements AdminServices{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    @Autowired
    public AdminServicesImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;

    }

    @Override
    public ActionSuccessResponse deleteUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));

        if (isAdmin) {
            throw new InvalidUserException("Cannot delete a user with the ADMIN role.");
        }

        userRepository.delete(user);

        return new ActionSuccessResponse(email, "User deleted successfully!");
    }

    @Override
    public ActionSuccessResponse grantAdminRole(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RoleEntity adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Role 'ADMIN' not found"));

        if (user.getRoles().contains(adminRole)) {
            throw new InvalidValueException("User already has the ADMIN role.");
        }

        user.getRoles().add(adminRole);
        userRepository.save(user);

        return new ActionSuccessResponse(email, "Granted ADMIN role successfully!");
    }

    @Override
    public ActionSuccessResponse revokeAdminRole(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RoleEntity adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Role 'ADMIN' not found"));

        if (!user.getRoles().contains(adminRole)) {
            throw new InvalidValueException("User does not have the ADMIN role.");
        }

        user.getRoles().remove(adminRole);
        userRepository.save(user);

        return new ActionSuccessResponse(email, "Revoked ADMIN role successfully!");
    }


}
