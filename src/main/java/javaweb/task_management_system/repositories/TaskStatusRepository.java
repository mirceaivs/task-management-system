package javaweb.task_management_system.repositories;

import javaweb.task_management_system.models.TaskStatusEntity;
import org.springframework.data.repository.CrudRepository;

public interface TaskStatusRepository extends CrudRepository<TaskStatusEntity, Long> {
}
