package javaweb.task_management_system.services;

import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.NotificationDTO;
import javaweb.task_management_system.models.NotificationEntity;
import javaweb.task_management_system.models.UserEntity;

import java.util.List;
import java.util.Set;

public interface NotificationService {
    ActionSuccessResponse addNotification(String content, UserEntity recipient);
    List<NotificationDTO> getNotificationsForUser();
    ActionSuccessResponse markNotificationAsRead(Long notificationId);
    int countUnreadNotifications();
    ActionSuccessResponse deleteNotification(Long notificationId);
}
