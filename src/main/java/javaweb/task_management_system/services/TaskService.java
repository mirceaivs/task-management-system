package javaweb.task_management_system.services;

import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.TaskDetailsDTO;
import javaweb.task_management_system.models.TaskEntity;

import java.time.LocalDateTime;
import java.util.List;


public interface TaskService {
    ActionSuccessResponse addTask(TaskEntity task);
    ActionSuccessResponse deleteTask(Long taskId);
    ActionSuccessResponse updateTask(Long taskId, TaskEntity updatedTask);
    List<TaskDetailsDTO> getTasksForCurrentUser();
    ActionSuccessResponse updateTaskStatus(Long taskId, Long statusId);
    ActionSuccessResponse getTasksProgress();
    List<TaskDetailsDTO> getFilteredTasksForCurrentUser(String projectName, Long statusId, LocalDateTime dueDate);
}
