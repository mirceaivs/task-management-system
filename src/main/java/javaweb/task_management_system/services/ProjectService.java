package javaweb.task_management_system.services;

import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.ProjectDetailsDTO;
import javaweb.task_management_system.models.ProjectEntity;

import java.util.List;


public interface ProjectService {
    ActionSuccessResponse addProject(ProjectEntity project);
    ActionSuccessResponse deleteProject(Long projectId);
    ActionSuccessResponse updateProject(Long projectId, ProjectEntity project);
    List<ProjectDetailsDTO> getProjectsForCurrentUserWithTasks();

}
