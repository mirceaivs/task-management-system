package javaweb.task_management_system.repositories;

import javaweb.task_management_system.models.TaskEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends CrudRepository<TaskEntity, Long> {

//    @Query("SELECT t FROM TaskEntity t WHERE t.project.owner.email = :userEmail")
//    List<TaskEntity> findByProjectOwnerEmail(@Param("userEmail") String userEmail);

    List<TaskEntity> findByAssigneesEmail(String email);
}
