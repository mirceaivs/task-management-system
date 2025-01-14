package javaweb.task_management_system.dtos;

import java.util.List;

public class ProjectDetailsDTO {
    private Long id;
    private String name;
    private String description;
    private List<TaskDetailsDTO> tasks;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<TaskDetailsDTO> getTasks() {
        return tasks;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTasks(List<TaskDetailsDTO> tasks) {
        this.tasks = tasks;
    }


}
