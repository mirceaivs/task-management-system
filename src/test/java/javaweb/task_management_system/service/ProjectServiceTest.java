package javaweb.task_management_system.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.ProjectDetailsDTO;
import javaweb.task_management_system.exceptions.InvalidValueException;
import javaweb.task_management_system.exceptions.ResourceNotFoundException;
import javaweb.task_management_system.exceptions.InvalidUserException;
import javaweb.task_management_system.models.ProjectEntity;
import javaweb.task_management_system.models.UserEntity;
import javaweb.task_management_system.repositories.ProjectRepository;
import javaweb.task_management_system.repositories.UserRepository;
import javaweb.task_management_system.services.NotificationService;
import javaweb.task_management_system.services.TokenService;
import javaweb.task_management_system.services.UserService;
import javaweb.task_management_system.services.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private UserEntity mockUser;
    private ProjectEntity mockProject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new UserEntity();
        mockUser.setEmail("owner@example.com");

        mockProject = new ProjectEntity("Test Project", "A description of the project", mockUser);
        mockProject.setId(1L);
    }

    @Test
    void addProject_shouldReturnSuccess_whenValidData() {
        // Arrange
        when(tokenService.getEmail()).thenReturn("owner@example.com");
        when(userService.findByEmail("owner@example.com")).thenReturn(mockUser);
        when(projectRepository.save(mockProject)).thenReturn(mockProject);

        // Act
        ActionSuccessResponse response = projectService.addProject(mockProject);

        // Assert
        assertEquals("owner@example.com", response.getEmail());
        assertTrue(response.getMessage().contains("Project added"));
        verify(notificationService, times(1)).addNotification(anyString(), eq(mockUser));
    }

    @Test
    void deleteProject_shouldThrowException_whenProjectNotFound() {
        // Arrange
        Long projectId = 999L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> projectService.deleteProject(projectId));
    }

    @Test
    void deleteProject_shouldReturnSuccess_whenAuthorized() {
        // Arrange
        Long projectId = mockProject.getId();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(tokenService.getEmail()).thenReturn("owner@example.com");

        // Mocking the void method with doNothing()
        doNothing().when(projectRepository).delete(mockProject);
        // Act
        ActionSuccessResponse response = projectService.deleteProject(projectId);

        // Assert
        assertEquals("owner@example.com", response.getEmail());
        assertTrue(response.getMessage().contains("Project removed"));
        verify(notificationService, times(1)).addNotification(anyString(), eq(mockUser));
    }

    @Test
    void updateProject_shouldReturnSuccess_whenAuthorizedAndUpdated() {
        // Arrange
        Long projectId = mockProject.getId();
        ProjectEntity updatedProject = new ProjectEntity("Updated Project", "Updated Description");
        updatedProject.setOwner(mockUser);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(tokenService.getEmail()).thenReturn("owner@example.com");

        // Act
        ActionSuccessResponse response = projectService.updateProject(projectId, updatedProject);

        // Assert
        assertEquals("owner@example.com", response.getEmail());
        assertTrue(response.getMessage().contains("Project updated"));
        assertEquals("Updated Project", mockProject.getName());
        assertEquals("Updated Description", mockProject.getDescription());
        verify(projectRepository, times(1)).save(mockProject);
    }

    @Test
    void updateProject_shouldThrowException_whenNotAuthorized() {
        // Arrange
        Long projectId = mockProject.getId();
        ProjectEntity updatedProject = new ProjectEntity("Updated Project", "Updated Description");
        updatedProject.setOwner(new UserEntity());  // Not the original owner
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(tokenService.getEmail()).thenReturn("notowner@example.com");

        // Act & Assert
        assertThrows(InvalidValueException.class, () -> projectService.updateProject(projectId, updatedProject));
    }

    @Test
    void getProjectsForCurrentUserWithTasks_shouldReturnListOfProjects() {
        // Arrange
        when(tokenService.getEmail()).thenReturn("owner@example.com");
        when(projectRepository.findByOwnerEmail("owner@example.com")).thenReturn(List.of(mockProject));

        // Act
        List<ProjectDetailsDTO> projectDetails = projectService.getProjectsForCurrentUserWithTasks();

        // Assert
        assertNotNull(projectDetails);
        assertEquals(1, projectDetails.size());
        assertEquals(mockProject.getName(), projectDetails.get(0).getName());
    }
}
