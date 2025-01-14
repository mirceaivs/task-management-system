package javaweb.task_management_system.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.ProjectDetailsDTO;
import javaweb.task_management_system.exceptions.ErrorResponse;
import javaweb.task_management_system.models.ProjectEntity;
import javaweb.task_management_system.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_OWNER')")
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(summary = "Add a new project", description = "This endpoint adds a new project to the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project added successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionSuccessResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid project data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json")
            )

    })
    @PostMapping("/add")
    public ActionSuccessResponse addProject(@RequestBody @Valid ProjectEntity project) {
        return projectService.addProject(project);
    }

    @Operation(summary = "Delete a project", description = "This endpoint deletes a project by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionSuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authorized to delete this project",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/delete/{projectId}")
    public ActionSuccessResponse deleteProject(@PathVariable Long projectId) {
        return projectService.deleteProject(projectId);
    }

    @Operation(summary = "Update project details", description = "This endpoint allows updating the details of a project.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionSuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authorized to delete this project",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/update/{projectId}")
    public ActionSuccessResponse updateProject(@PathVariable Long projectId, @RequestBody ProjectEntity updatedProject) {
        return projectService.updateProject(projectId, updatedProject);
    }

    @Operation(summary = "Get projects for the current user", description = "Retrieve all projects assigned to the logged-in user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projects retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectDetailsDTO.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/details")
    public ResponseEntity<List<ProjectDetailsDTO>> getProjectsForCurrentUser() {
        List<ProjectDetailsDTO> projects = projectService.getProjectsForCurrentUserWithTasks();
        return ResponseEntity.ok(projects);
    }


}
