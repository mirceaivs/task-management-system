package javaweb.task_management_system.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.LoginRequest;
import javaweb.task_management_system.dtos.LoginResponseDTO;
import javaweb.task_management_system.dtos.UserDTO;
import javaweb.task_management_system.exceptions.ErrorResponse;
import javaweb.task_management_system.models.UserEntity;
import javaweb.task_management_system.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthenticationService authenticationService;

    @Autowired
    public AuthController(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Register a new user", description = "This endpoint registers a new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or role number",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "User already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserEntity user, @RequestParam int roleNumber) {
        UserDTO successResponse = authenticationService.register(user, roleNumber);
        return ResponseEntity.ok(successResponse);
    }

    @Operation(summary = "User login", description = "This endpoint allows a user to login with email and password by creating a RSA encrypted JWT token stored in cookies.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request (invalid login details or validation errors)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized (invalid credentials)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest user, HttpServletResponse response) {
        LoginResponseDTO successResponse = authenticationService.login(user.getEmail(), user.getPassword());
        Cookie cookie = new Cookie("jwt", successResponse.getJwt());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(15 * 60);
        response.addCookie(cookie);

        return ResponseEntity.ok(successResponse.getUser());
    }

    @Operation(summary = "User logout", description = "This endpoint logs out the current user by invalidating the JWT cookie.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully logged out",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionSuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request (invalid logout request)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Resource not found (authentication context or JWT not found)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        ActionSuccessResponse responseStatus = authenticationService.logout();
        return ResponseEntity.ok(responseStatus);
    }

}
