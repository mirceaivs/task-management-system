package javaweb.task_management_system.controllers;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.AdminUserRequest;
import javaweb.task_management_system.dtos.UserDTO;
import javaweb.task_management_system.services.AdminServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminServices adminServices;

    @Autowired
    public AdminController(AdminServices adminServices) {
        this.adminServices = adminServices;
    }

    @Operation(summary = "Delete a user by email", description = "This endpoint allows the admin to delete a user by their email address.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully deleted.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionSuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid operation (trying to delete an admin user).", content = @Content(mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json")
            ),
    })
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        return ResponseEntity.ok(adminServices.deleteUserByEmail(email));
    }

    @Operation(summary = "Grant admin role to a user", description = "This endpoint allows the admin to grant admin privileges to a user by their email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin role granted successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionSuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "User or admin role not found.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid operation.", content = @Content(mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json")
            ),
    })
    @PatchMapping("/users/{email}/grant-admin")
    public ResponseEntity<?> grantAdminRole(@PathVariable String email) {
        return ResponseEntity.ok(adminServices.grantAdminRole(email));
    }

    @Operation(summary = "Revoke admin role from a user", description = "This endpoint allows the admin to revoke admin privileges from a user by their email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin role revoked successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionSuccessResponse.class))),
            @ApiResponse(responseCode = "404", description = "User or admin role not found.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid operation.", content = @Content(mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json")
            ),
    })
    @PatchMapping("/users/{email}/revoke-admin")
    public ResponseEntity<?> revokeAdminRole(@PathVariable String email) {
        return ResponseEntity.ok(adminServices.revokeAdminRole(email));
    }

}
