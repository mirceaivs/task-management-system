


package javaweb.task_management_system.services;


import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.UserDTO;
import javaweb.task_management_system.exceptions.InvalidValueException;
import javaweb.task_management_system.exceptions.ResourceNotFoundException;
import javaweb.task_management_system.models.UserEntity;
import javaweb.task_management_system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;

    }

    @Override
    public ActionSuccessResponse deleteUser() {
        String email = tokenService.getEmail();
        UserEntity user = findByEmail(email);

        if (user != null) {
            userRepository.delete(user);
            return new ActionSuccessResponse(email, "Your account has been successfully deleted!");
        } else {
            throw new ResourceNotFoundException("User not found.");
        }
    }

    @Override
    public ActionSuccessResponse updateUser(UserEntity updatedUser) {
        String email = tokenService.getEmail();
        UserEntity user = findByEmail(email);

        if (updatedUser.getName() != null && !updatedUser.getName().isBlank()) {
            user.setName(updatedUser.getName());
        }
        if (updatedUser.getLastname() != null && !updatedUser.getLastname().isBlank()) {
            user.setLastName(updatedUser.getLastname());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isBlank()) {
            user.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        userRepository.save(user);
        return new ActionSuccessResponse(user.getEmail(), "User updated successfully!");
    }

    @Override
    public UserEntity findByEmail(String email) {
        if(email == null)
            throw new InvalidValueException("Email value not found!");
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

    }

    @Override
    public UserDTO convertToDto(UserEntity user){
        return new UserDTO(user);
    }

    @Override
    public UserEntity convertToEntity(UserDTO user){
        return new UserEntity(user.getId(), user.getName(), user.getLastname(), user.getEmail());
    }

}
