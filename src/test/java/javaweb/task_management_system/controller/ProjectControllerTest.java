package javaweb.task_management_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javaweb.task_management_system.controllers.AuthController;
import javaweb.task_management_system.controllers.ProjectController;
import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.ProjectDetailsDTO;
import javaweb.task_management_system.exceptions.GlobalExceptionHandler;
import javaweb.task_management_system.models.ProjectEntity;
import javaweb.task_management_system.services.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@ContextConfiguration(classes = {ProjectController.class, TestSecurityConfig.class, GlobalExceptionHandler.class})
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    private ProjectEntity sampleProject;
    private ActionSuccessResponse successResponse;

    @BeforeEach
    void setUp() {
        sampleProject = new ProjectEntity();
        sampleProject.setId(1L);
        sampleProject.setName("Test Project");
        sampleProject.setDescription("Test Description");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test_user", "password", List.of(new SimpleGrantedAuthority("ROLE_PROJECT_OWNER")))
        );

        successResponse = new ActionSuccessResponse("test@example.com", "Operation successful");
    }

    @Test
    public void addProject_ShouldReturnSuccessResponse() throws Exception {
        String requestBody = """
        {
             "name": "New Project",
             "description": "This project involves the development of a task management system. It aims to organize and track tasks effectively and efficiently. The project scope includes backend development, frontend implementation, and system integration."
        }
    """;

        Mockito.when(projectService.addProject(any(ProjectEntity.class))).thenReturn(successResponse);

        mockMvc.perform(post("/api/projects/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(successResponse.getEmail()))
                .andExpect(jsonPath("$.message").value(successResponse.getMessage()));
    }

    @Test
    public void deleteProject_ShouldReturnSuccessResponse() throws Exception {
        Mockito.when(projectService.deleteProject(anyLong())).thenReturn(successResponse);

        mockMvc.perform(delete("/api/projects/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(successResponse.getEmail()))
                .andExpect(jsonPath("$.message").value(successResponse.getMessage()));
    }

    @Test
    public void updateProject_ShouldReturnSuccessResponse() throws Exception {
        Mockito.when(projectService.updateProject(anyLong(), any(ProjectEntity.class))).thenReturn(successResponse);

        mockMvc.perform(put("/api/projects/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProject)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(successResponse.getEmail()))
                .andExpect(jsonPath("$.message").value(successResponse.getMessage()));
    }

    @Test
    public void getProjectsForCurrentUser_ShouldReturnProjectList() throws Exception {
        ProjectDetailsDTO projectDetailsDTO = new ProjectDetailsDTO();
        projectDetailsDTO.setId(1L);
        projectDetailsDTO.setName("Test Project");
        projectDetailsDTO.setDescription("Test Description");
        projectDetailsDTO.setTasks(List.of()); // Assuming no tasks for simplicity

        Mockito.when(projectService.getProjectsForCurrentUserWithTasks())
                .thenReturn(Arrays.asList(projectDetailsDTO));

        mockMvc.perform(get("/api/projects/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(projectDetailsDTO.getId()))
                .andExpect(jsonPath("$[0].name").value(projectDetailsDTO.getName()))
                .andExpect(jsonPath("$[0].description").value(projectDetailsDTO.getDescription()));
    }


}
