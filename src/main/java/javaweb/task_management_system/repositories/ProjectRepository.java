package javaweb.task_management_system.repositories;

import javaweb.task_management_system.models.ProjectEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends CrudRepository<ProjectEntity, Long> {

    @EntityGraph(attributePaths = {"tasks", "tasks.status"})
    @Query("SELECT p FROM ProjectEntity p WHERE p.owner.email = :userEmail")
    List<ProjectEntity> findByOwnerEmail(@Param("userEmail") String userEmail);


}
