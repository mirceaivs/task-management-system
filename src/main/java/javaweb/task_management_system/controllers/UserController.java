package javaweb.task_management_system.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.models.UserEntity;
import javaweb.task_management_system.services.AuthenticationService;
import javaweb.task_management_system.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")

public class UserController {

    private final UserService userService;
    private final AuthenticationService authService;

    @Autowired
    public UserController(UserService userService, AuthenticationService authService) {
        this.userService = userService;
        this.authService = authService;
    }


    @Operation(summary = "Delete the user", description = "Delete the current logged-in user.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(mediaType = "application/json")
            )
    })
    @DeleteMapping("/user/delete")
    public ResponseEntity<ActionSuccessResponse> deleteUser() {
        ActionSuccessResponse response = userService.deleteUser();
        authService.logout();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user details", description = "Allows updating details for the current logged-in user.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json")
            ),
    })
    @PatchMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserEntity updatedUser) {
        return ResponseEntity.ok(userService.updateUser(updatedUser));
    }
}
