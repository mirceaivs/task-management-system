package javaweb.task_management_system.repositories;

import javaweb.task_management_system.models.NotificationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface NotificationRepository extends CrudRepository<NotificationEntity, Long> {

    Set<NotificationEntity> findByRecipientEmailOrderByCreatedAtDesc(String recipientEmail);
    int countByRecipientEmailAndIsReadFalse(String recipientEmail);
}
