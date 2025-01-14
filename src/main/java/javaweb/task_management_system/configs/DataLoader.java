package javaweb.task_management_system.configs;


import jakarta.annotation.PostConstruct;
import javaweb.task_management_system.models.RoleEntity;
import javaweb.task_management_system.models.TaskStatusEntity;
import javaweb.task_management_system.repositories.RoleRepository;
import javaweb.task_management_system.repositories.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    private final RoleRepository roleRepository;
    private final TaskStatusRepository taskStatusRepository;

    @Autowired
    public DataLoader(RoleRepository roleRepository, TaskStatusRepository taskStatusRepository) {
        this.roleRepository = roleRepository;
        this.taskStatusRepository = taskStatusRepository;
    }

    @PostConstruct
    public void init(){
        roleRepository.save(new RoleEntity("ADMIN"));
        roleRepository.save(new RoleEntity("USER"));
        roleRepository.save(new RoleEntity("PROJECT_OWNER"));
        taskStatusRepository.save(new TaskStatusEntity("TODO"));
        taskStatusRepository.save(new TaskStatusEntity("IN_PROGRESS"));
        taskStatusRepository.save(new TaskStatusEntity("COMPLETED"));
    }
}
