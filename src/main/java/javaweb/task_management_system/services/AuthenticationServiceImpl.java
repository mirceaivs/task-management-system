package javaweb.task_management_system.services;

import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.LoginResponseDTO;
import javaweb.task_management_system.dtos.UserDTO;
import javaweb.task_management_system.exceptions.InvalidUserException;
import javaweb.task_management_system.exceptions.InvalidValueException;
import javaweb.task_management_system.exceptions.ResourceNotFoundException;
import javaweb.task_management_system.models.RoleEntity;
import javaweb.task_management_system.models.UserEntity;
import javaweb.task_management_system.repositories.RoleRepository;
import javaweb.task_management_system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class AuthenticationServiceImpl implements AuthenticationService{

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserService userService;

    @Autowired
    public AuthenticationServiceImpl(TokenService tokenService, UserRepository userRepository, PasswordEncoder passwordEncoder,
                                     RoleRepository roleRepository, UserService userService) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @Override
    public UserDTO register(UserEntity user, int roleNumber) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new InvalidUserException("User exists!");
        }

        RoleEntity userRole;
        if (userRepository.count() == 0) {
            if (roleNumber != 1 && roleNumber != 2) {
                throw new InvalidUserException("Invalid role number for first user, valid values are 1 or 2");
            }
            userRole = getRoleByName("ADMIN");
        } else {
            switch (roleNumber) {
                case 1:
                    userRole = getRoleByName("USER");
                    break;
                case 2:
                    userRole = getRoleByName("PROJECT_OWNER");
                    break;
                default:
                    throw new InvalidUserException("Invalid role number");
            }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.getRoles().add(userRole);

        userRepository.save(user);

        return new UserDTO(user);
    }


    @Override
    public LoginResponseDTO login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidUserException("Credentials are incorrect"));

        if(!passwordEncoder.matches(password, user.getPassword()))
            throw new InvalidUserException("Credentials are incorrect!");

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());

        Authentication auth = new UsernamePasswordAuthenticationToken(email, password, authorities);

        String token = tokenService.generateJwt(auth);

        return new LoginResponseDTO(new UserDTO(user), token);
    }


    @Override
    public ActionSuccessResponse logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication is null or an anonymous user
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResourceNotFoundException("Authentication context is not present!");
        }

        // Check if principal is an instance of Jwt (for JWT-based authentication)
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();  // Safe cast to Jwt
            String subject = jwt.getClaimAsString("sub");    // Extract the subject from the JWT

            if (subject == null) {
                throw new ResourceNotFoundException("Subject claim is missing in the JWT!");
            }

            // Clear the security context to log the user out
            SecurityContextHolder.clearContext();
            return new ActionSuccessResponse(subject, "Logout successfully ");
        } else {
            // Handle cases where principal is not a JWT (it could be a String, for example)
            throw new ResourceNotFoundException("Authentication principal is not a valid JWT!");
        }
    }


    private RoleEntity getRoleByName(String role) {
        if(role == null)
            throw new InvalidValueException("Role value not found!");
        return roleRepository.findByName(role).orElseThrow(() -> new ResourceNotFoundException("Role " + role + " doesn't exist!"));
    }



}
