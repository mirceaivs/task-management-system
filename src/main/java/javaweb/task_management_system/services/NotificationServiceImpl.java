package javaweb.task_management_system.services;

import jakarta.transaction.Transactional;
import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.NotificationDTO;
import javaweb.task_management_system.dtos.UserDTO;
import javaweb.task_management_system.exceptions.ResourceNotFoundException;
import javaweb.task_management_system.models.NotificationEntity;
import javaweb.task_management_system.models.UserEntity;
import javaweb.task_management_system.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final TokenService tokenService;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, TokenService tokenService) {
        this.notificationRepository = notificationRepository;
        this.tokenService = tokenService;
    }

    @Override
    public ActionSuccessResponse addNotification(String content, UserEntity recipient) {
        String userEmail = tokenService.getEmail();
        NotificationEntity notification = new NotificationEntity(content, recipient);
        notificationRepository.save(notification);

        return new ActionSuccessResponse(userEmail, "Notification added! " + notification.getId());
    }

    @Override
    public List<NotificationDTO> getNotificationsForUser() {
        String userEmail = tokenService.getEmail();
        Set<NotificationEntity> notifications = notificationRepository.findByRecipientEmailOrderByCreatedAtDesc(userEmail);


        return notifications.stream()
                .sorted(Comparator.comparing(NotificationEntity::getCreatedAt).reversed())
                .map(notification -> new NotificationDTO(
                        notification.getId(),
                        notification.getContent(),
                        notification.isRead(),
                        notification.getCreatedAt(),
                        new UserDTO(notification.getRecipient())
                ))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public ActionSuccessResponse markNotificationAsRead(Long notificationId) {
        String userEmail = tokenService.getEmail();
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);

        return new ActionSuccessResponse(userEmail, "Notification marked as read! " + notificationId);
    }

    @Override
    public int countUnreadNotifications() {
        String userEmail = tokenService.getEmail();
        return notificationRepository.countByRecipientEmailAndIsReadFalse(userEmail);
    }

    @Override
    public ActionSuccessResponse deleteNotification(Long notificationId) {
        String userEmail = tokenService.getEmail();
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Notification not found!");
        }

        notificationRepository.deleteById(notificationId);

        return new ActionSuccessResponse(userEmail, "Notification deleted! " + notificationId);
    }
}
