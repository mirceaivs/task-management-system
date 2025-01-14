package javaweb.task_management_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javaweb.task_management_system.controllers.AuthController;
import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.LoginRequest;
import javaweb.task_management_system.dtos.LoginResponseDTO;
import javaweb.task_management_system.dtos.UserDTO;
import javaweb.task_management_system.exceptions.GlobalExceptionHandler;
import javaweb.task_management_system.exceptions.InvalidUserException;
import javaweb.task_management_system.models.RoleEntity;
import javaweb.task_management_system.models.UserEntity;
import javaweb.task_management_system.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {AuthController.class, TestSecurityConfig.class, GlobalExceptionHandler.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserEntity testUser;
    private UserDTO testUserDTO;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1L);  // Set an ID if necessary
        testUser.setName("mircea");
        testUser.setLastName("mircel");
        testUser.setEmail("mircea@yahoo.com");
        testUser.setPassword("rootrootroot");


        testUserDTO = new UserDTO(testUser);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("mircea@yahoo.com");
        loginRequest.setPassword("rootrootroot");
    }
    //        when(authenticationService.register(any(UserEntity.class), eq(1)))
//                .thenReturn(testUserDTO);
//        System.out.println("Request Body: " + objectMapper.writeValueAsString(testUser));
//        mockMvc.perform(post("/api/auth/register")
//                        .param("roleNumber", "1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(testUser)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
//                .andExpect(jsonPath("$.name").value(testUser.getName()))
//                .andExpect(jsonPath("$.lastname").value(testUser.getLastname()));
    @Test
    @WithMockUser
    void registerSuccess() throws Exception {

        String requestBody = """
        {
            "name": "mircea",
            "lastname": "mircel",
            "email": "mircea@yahoo.com",
            "password": "rootrootroot"
        }
    """;

        // Mocked roles for the response
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setName("ADMIN");

        // Mocked roles set
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);

        // Setting roles to the mocked UserEntity
        testUser.setRoles(roles);


        when(authenticationService.register(any(UserEntity.class), eq(1)))
                .thenReturn(testUserDTO);


        // Performing the test request
        mockMvc.perform(post("/api/auth/register")
                        .param("roleNumber", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("mircea"))
                .andExpect(jsonPath("$.lastname").value("mircel"))
                .andExpect(jsonPath("$.email").value("mircea@yahoo.com"));
    }

    @Test
    @WithMockUser
    void registerWithInvalidRole() throws Exception {
        String requestBody = """
        {
            "name": "mircea",
            "lastname": "mircel",
            "email": "mircea@yahoo.com",
            "password": "rootrootroot"
        }
    """;
        when(authenticationService.register(any(UserEntity.class), eq(4))) // invalid role number 4
                .thenThrow(new InvalidUserException("Invalid role number for first user, valid values are 1 or 2"));

        mockMvc.perform(post("/api/auth/register")
                        .param("roleNumber", "4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginSuccess() throws Exception {
        LoginResponseDTO loginResponse = new LoginResponseDTO(testUserDTO, "test.jwt.token");
        when(authenticationService.login(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(loginResponse);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("jwt"))
                .andExpect(cookie().httpOnly("jwt", true))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andReturn();

        String jwt = result.getResponse().getCookie("jwt").getValue();
        assert jwt != null && jwt.equals("test.jwt.token");
    }

    @Test
    void loginWithInvalidCredentials() throws Exception {
        // Simulate the exception being thrown during login
        when(authenticationService.login(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenThrow(new InvalidUserException("Credentials are incorrect"));

        // Perform the login request and expect a BadRequest response
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())  // Expecting a 400 BadRequest status
                .andExpect(jsonPath("$.message").value("Credentials are incorrect"));  // Expecting the error message
    }


        @Test
    @WithMockUser
    void logoutSuccess() throws Exception {
        ActionSuccessResponse logoutResponse = new ActionSuccessResponse("mircea@yahoo.com", "Logout successfully");
        when(authenticationService.logout()).thenReturn(logoutResponse);

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("jwt", (String) null))
                .andExpect(cookie().maxAge("jwt", 0))
                .andExpect(jsonPath("$.email").value("mircea@yahoo.com"))
                .andExpect(jsonPath("$.message").value("Logout successfully"));
    }

    @Test
    void logoutWithoutAuthentication() throws Exception {
        // No @WithMockUser annotation here
        when(authenticationService.logout())
                .thenThrow(new RuntimeException("Authentication context is not present!"));

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized());  // Changed from isInternalServerError to isUnauthorized
    }
}