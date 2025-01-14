package javaweb.task_management_system.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.TaskDetailsDTO;
import javaweb.task_management_system.exceptions.ErrorResponse;
import javaweb.task_management_system.models.TaskEntity;
import javaweb.task_management_system.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_OWNER')")
@RequestMapping("/api/tasks")

public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Add a new task", description = "This endpoint adds a new task to the system.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task added successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized (invalid credentials)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (access denied)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/add")
    public ActionSuccessResponse addTask(@RequestBody @Valid TaskEntity task) {
        return taskService.addTask(task);
    }

    @Operation(summary = "Delete a task", description = "This endpoint deletes a task by its ID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized (invalid credentials)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (access denied)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/delete/{taskId}")
    public ActionSuccessResponse deleteTask(@PathVariable Long taskId) {
        return taskService.deleteTask(taskId);
    }

    @Operation(summary = "Update task details", description = "This endpoint allows updating the details of a task.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized (invalid credentials)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden (access denied)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/update/{taskId}")
    public ActionSuccessResponse updateTask(@PathVariable Long taskId, @RequestBody TaskEntity updatedTask) {
        return taskService.updateTask(taskId, updatedTask);
    }

    @Operation(summary = "Get tasks for the current user", description = "Retrieve all tasks assigned to the logged-in user.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tasks retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDetailsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized (invalid credentials)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/details")
    public ResponseEntity<List<TaskDetailsDTO>> getTasksForCurrentUser() {
        List<TaskDetailsDTO> tasks = taskService.getTasksForCurrentUser();
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Update task status", description = "This endpoint allows updating the status of a task.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tasks Status updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDetailsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized (invalid credentials)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PatchMapping("/update-status/{taskId}")
    public ActionSuccessResponse updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam Long updatedTaskStatusId // Accept the status ID as a query parameter
    ) {
        return taskService.updateTaskStatus(taskId, updatedTaskStatusId);
    }

    @Operation(summary = "Get filtered tasks", description = "Retrieve tasks filtered by project, status, or due date for the current user.")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Filtered tasks retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDetailsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized (invalid credentials)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/filtered")
    public ResponseEntity<List<TaskDetailsDTO>> getFilteredTasks(
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) Long statusId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate) {

        List<TaskDetailsDTO> filteredTasks = taskService.getFilteredTasksForCurrentUser(projectName, statusId, dueDate);

        return ResponseEntity.ok(filteredTasks);
    }

    @Operation(summary = "Get task progress", description = "Retrieve the progress summary of tasks for the current user.")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task progress retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized (invalid credentials)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/progress")
    public ActionSuccessResponse getTaskProgress() {
        return taskService.getTasksProgress();
    }


}
