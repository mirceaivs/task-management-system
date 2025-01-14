package javaweb.task_management_system.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.exceptions.ErrorResponse;
import javaweb.task_management_system.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


import javaweb.task_management_system.dtos.NotificationDTO;


import java.util.Map;


@RestController
@RequestMapping("/api/notifications")

public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Get notifications for the current user", description = "Retrieve all notifications for the logged-in user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificationDTO.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "404", description = "No notifications found for the user",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotificationsForUser() {
        List<NotificationDTO> notifications = notificationService.getNotificationsForUser();
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "Mark a notification as read", description = "Marks a specific notification as read by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification marked as read",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionSuccessResponse.class))),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "404", description = "Notification not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.markNotificationAsRead(notificationId));
    }

    @Operation(summary = "Get unread notifications count", description = "Get the count of unread notifications for the current user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unread notifications count fetched successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount() {
        int unreadCount = notificationService.countUnreadNotifications();
        return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
    }

    @Operation(summary = "Delete a notification", description = "Delete a notification by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActionSuccessResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "404", description = "Notification not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @DeleteMapping("/delete/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.deleteNotification(notificationId));
    }
}

