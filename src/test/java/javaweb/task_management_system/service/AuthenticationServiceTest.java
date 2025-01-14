package javaweb.task_management_system.service;

import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.LoginRequest;
import javaweb.task_management_system.dtos.LoginResponseDTO;
import javaweb.task_management_system.dtos.UserDTO;
import javaweb.task_management_system.exceptions.InvalidUserException;
import javaweb.task_management_system.exceptions.ResourceNotFoundException;
import javaweb.task_management_system.models.RoleEntity;
import javaweb.task_management_system.models.UserEntity;
import javaweb.task_management_system.repositories.RoleRepository;
import javaweb.task_management_system.repositories.UserRepository;
import javaweb.task_management_system.services.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private TokenServiceImpl tokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserServiceImpl userService;

    @Test
    public void register_shouldRegisterUserSuccessfully_whenValidInput() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        user.setPassword("securepassword123");

        RoleEntity role = new RoleEntity();
        role.setName("USER");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.count()).thenReturn(5L);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(user.getPassword())).thenReturn("hashedPassword");

        // Act
        UserDTO result = authenticationService.register(user, 1);

        // Assert
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).save(user); // Verify save was called
    }

    @Test
    public void register_shouldThrowException_whenEmailAlreadyExists() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(InvalidUserException.class, () -> authenticationService.register(user, 1));
        verify(userRepository, never()).save(any());
    }

    @Test
    public void register_shouldThrowException_whenInvalidRoleNumber() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.count()).thenReturn(5L);

        // Act & Assert
        assertThrows(InvalidUserException.class, () -> authenticationService.register(user, 99));
        verify(userRepository, never()).save(any());
    }


    @Test
    public void login_shouldReturnJwt_whenValidCredentials() {
        // Arrange
        String email = "test@example.com";
        String password = "securepassword123";

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword("hashedPassword");

        RoleEntity role = new RoleEntity();
        role.setName("USER");
        user.getRoles().add(role);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(tokenService.generateJwt(any())).thenReturn("mockJwtToken");

        // Act
        LoginResponseDTO response = authenticationService.login(email, password);

        // Assert
        assertNotNull(response.getJwt());
        assertEquals(email, response.getUser().getEmail());
        verify(tokenService).generateJwt(any(Authentication.class));
    }

    @Test
    public void login_shouldThrowException_whenInvalidPassword() {
        // Arrange
        String email = "test@example.com";
        String password = "wrongpassword";

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword("hashedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidUserException.class, () -> authenticationService.login(email, password));
        verify(tokenService, never()).generateJwt(any());
    }

    @Test
    public void login_shouldThrowException_whenUserDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidUserException.class, () -> authenticationService.login(email, "password"));
    }

    @Test
    public void logout_shouldClearSecurityContext_whenAuthenticated() {
        // Arrange
        // Mock the Jwt object
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sub")).thenReturn("test@example.com");  // Mock the subject claim

        // Create the Authentication token with the mocked Jwt
        Authentication authentication = new UsernamePasswordAuthenticationToken(jwt, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        ActionSuccessResponse response = authenticationService.logout();

        // Assert
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Logout successfully", response.getMessage().trim());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }


    @Test
    public void logout_shouldThrowException_whenNotAuthenticated() {
        // Arrange
        SecurityContextHolder.clearContext();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> authenticationService.logout());
    }



}
