package javaweb.task_management_system.services;

import jakarta.transaction.Transactional;
import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.ProjectDetailsDTO;
import javaweb.task_management_system.dtos.TaskDetailsDTO;
import javaweb.task_management_system.exceptions.InvalidUserException;
import javaweb.task_management_system.exceptions.InvalidValueException;
import javaweb.task_management_system.exceptions.ResourceNotFoundException;
import javaweb.task_management_system.models.ProjectEntity;
import javaweb.task_management_system.models.UserEntity;
import javaweb.task_management_system.repositories.ProjectRepository;
import javaweb.task_management_system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements  ProjectService{

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, UserService userService, TokenService tokenService, UserRepository userRepository, NotificationService notificationService) {
        this.projectRepository = projectRepository;
        this.notificationService = notificationService;
        this.userService = userService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public ActionSuccessResponse addProject(ProjectEntity project) {
        String ownerEmail = tokenService.getEmail();

        UserEntity owner = userService.findByEmail(ownerEmail);
        project.setOwner(owner);
        projectRepository.save(project);
        notificationService.addNotification("Project: " + project.getName() + " has been added!", owner);
        return new ActionSuccessResponse(owner.getEmail(), "Project added! " + project.getId());
    }

    @Override
    public ActionSuccessResponse deleteProject(Long projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        String ownerEmail = tokenService.getEmail();
        if (!project.getOwner().getEmail().equals(ownerEmail)) {
            throw new InvalidValueException("You are not authorized to delete this project");
        }
        String projectName = project.getName();
        UserEntity owner = project.getOwner();
        projectRepository.delete(project);
        notificationService.addNotification("Project: " + projectName + " has been deleted!", owner);
        return new ActionSuccessResponse(ownerEmail, "Project removed " + projectId);
    }

    @Override
    @Transactional
    public ActionSuccessResponse updateProject(Long projectId, ProjectEntity updatedProject) {
        boolean isUpdated = false;
        ProjectEntity existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        String ownerEmail = tokenService.getEmail();
        if (!existingProject.getOwner().getEmail().equals(ownerEmail)) {
            throw new InvalidValueException("You are not authorized to update this project");
        }
        if(updatedProject.getName() != null && !updatedProject.getName().isBlank())
        {
            existingProject.setName(updatedProject.getName());
            isUpdated = true;
        }
        if(updatedProject.getDescription() != null && !updatedProject.getDescription().isBlank()){
            existingProject.setDescription(updatedProject.getDescription());
            isUpdated = true;
        }

        if (updatedProject.getOwner() != null) {
            if (!existingProject.getOwner().equals(updatedProject.getOwner())) {

                UserEntity oldOwner = existingProject.getOwner();
                UserEntity newOwner = updatedProject.getOwner();

                if (!userRepository.existsById(newOwner.getId())) {
                    throw new ResourceNotFoundException("New owner does not exist");
                }

                existingProject.setOwner(newOwner);

                notificationService.addNotification("You are now the owner of the project: " + existingProject.getName(), newOwner);

                if (!oldOwner.equals(newOwner)) {
                    notificationService.addNotification("You are no longer the owner of the project: " + existingProject.getName(), oldOwner);
                }
                isUpdated = true;
            }
        }

        if(isUpdated){
            UserEntity oldOwner = existingProject.getOwner();
            projectRepository.save(existingProject);

            if (existingProject.getOwner() != null && !existingProject.getOwner().equals(updatedProject.getOwner())) {
                notificationService.addNotification("Project: " + existingProject.getName() + " has been updated!", existingProject.getOwner());
            }

        }


        return new ActionSuccessResponse(ownerEmail, "Project updated! " + projectId);
    }


    @Override
    public List<ProjectDetailsDTO> getProjectsForCurrentUserWithTasks() {

        String userEmail = tokenService.getEmail();

        List<ProjectEntity> projects = projectRepository.findByOwnerEmail(userEmail);

        return projects.stream().map(project -> {
            ProjectDetailsDTO projectDetails = new ProjectDetailsDTO();
            projectDetails.setId(project.getId());
            projectDetails.setName(project.getName());
            projectDetails.setDescription(project.getDescription());

            List<TaskDetailsDTO> taskDetails = (project.getTasks() != null ? project.getTasks().stream()
                    .map(task -> {
                        TaskDetailsDTO taskDetail = new TaskDetailsDTO();
                        taskDetail.setId(task.getId());
                        taskDetail.setName(task.getName());
                        taskDetail.setStatus(task.getStatus().getName());

                        List<String> assigneeEmails = task.getAssignees().stream()
                                .filter(assignee -> assignee.getEmail() != null)
                                .map(assignee -> assignee.getEmail())
                                .collect(Collectors.toList());

                        taskDetail.setAssignedUsers(assigneeEmails);

                        taskDetail.setTaskOwner(task.getProject().getOwner().getEmail());
                        taskDetail.setProjectName(project.getName());
                        taskDetail.setDueDate(task.getDueDate());

                        return taskDetail;
                    }).collect(Collectors.toList()) : new ArrayList<>());

            projectDetails.setTasks(taskDetails);
            return projectDetails;
        }).collect(Collectors.toList());
    }



}
