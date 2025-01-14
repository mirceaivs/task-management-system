package javaweb.task_management_system.services;

import jakarta.transaction.Transactional;
import javaweb.task_management_system.dtos.ActionSuccessResponse;
import javaweb.task_management_system.dtos.TaskDetailsDTO;
import javaweb.task_management_system.exceptions.InvalidUserException;
import javaweb.task_management_system.exceptions.ResourceNotFoundException;
import javaweb.task_management_system.models.ProjectEntity;
import javaweb.task_management_system.models.TaskEntity;
import javaweb.task_management_system.models.TaskStatusEntity;
import javaweb.task_management_system.models.UserEntity;
import javaweb.task_management_system.repositories.ProjectRepository;
import javaweb.task_management_system.repositories.TaskRepository;
import javaweb.task_management_system.repositories.TaskStatusRepository;
import javaweb.task_management_system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class TaskServiceImpl implements TaskService{

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository, TaskStatusRepository taskStatusRepository, TokenService tokenService, UserRepository userRepository, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
        this.projectRepository = projectRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    @Override
    public ActionSuccessResponse addTask(TaskEntity task) {

        if (!projectRepository.existsById(task.getProject().getId())) {
            throw new ResourceNotFoundException("Project not found");
        }

        if (!taskStatusRepository.existsById(task.getStatus().getId())) {
            throw new ResourceNotFoundException("Status not found");
        }

        taskRepository.save(task);

        String userEmail = tokenService.getEmail();
        UserEntity user = userRepository.findByEmail(userEmail).orElseThrow(() -> new InvalidUserException("User not found!"));

        notificationService.addNotification("Task: " + task.getName() + " has been added!", user);

        return new ActionSuccessResponse(userEmail, "Task added successfully! " + task.getId());
    }

    @Override
    public ActionSuccessResponse deleteTask(Long taskId) {

        TaskEntity taskToBeRemoved = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found!"));

        String userEmail = tokenService.getEmail();
        String taskName = taskToBeRemoved.getName();


        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new InvalidUserException("User not found!"));


        taskToBeRemoved.getAssignees().forEach(assignee -> {
            notificationService.addNotification("Task: " + taskName + " has been deleted!", assignee);
        });


        taskRepository.delete(taskToBeRemoved);


        notificationService.addNotification("Task: " + taskName + " has been deleted!", user);

        return new ActionSuccessResponse(userEmail, "Task deleted successfully! " + taskId);
    }

    @Override
    public List<TaskDetailsDTO> getFilteredTasksForCurrentUser(String projectName, Long statusId, LocalDateTime dueDate) {
        String userEmail = tokenService.getEmail();

        List<TaskEntity> tasks = taskRepository.findByAssigneesEmail(userEmail);

        Stream<TaskEntity> filteredTasksStream = tasks.stream();

        if (projectName != null && !projectName.isBlank()) {
            filteredTasksStream = filteredTasksStream.filter(task -> task.getProject().getName().equalsIgnoreCase(projectName));
        }

        if (statusId != null) {
            filteredTasksStream = filteredTasksStream.filter(task -> task.getStatus().getId().equals(statusId));
        }

        if (dueDate != null) {
            filteredTasksStream = filteredTasksStream.filter(task -> task.getDueDate().toLocalDate().isEqual(dueDate.toLocalDate()));
        }

        return filteredTasksStream.map(task -> {
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
            taskDetail.setProjectName(task.getProject().getName());
            taskDetail.setDueDate(task.getDueDate());

            return taskDetail;
        }).collect(Collectors.toList());
    }



    @Override
    @Transactional
    public ActionSuccessResponse updateTask(Long taskId, TaskEntity updatedTask) {
        boolean isUpdated = false;

        TaskEntity existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found!"));

        String ownerEmail = existingTask.getProject().getOwner().getEmail();
        String userEmail = tokenService.getEmail();

        if (!ownerEmail.equals(userEmail)) {
            throw new InvalidUserException("You are not authorized to update this task");
        }

        if (updatedTask.getName() != null && !updatedTask.getName().isBlank()) {
            existingTask.setName(updatedTask.getName());
            isUpdated = true;
        }

        if (!updatedTask.getStatus().getId().equals(existingTask.getStatus().getId())) {
            existingTask.setStatus(updatedTask.getStatus());
            isUpdated = true;
        }


        if (updatedTask.getAssignees() != null) {
            Set<UserEntity> newAssignees = updatedTask.getAssignees();
            Set<UserEntity> oldAssignees = existingTask.getAssignees();


            for (UserEntity assignee : newAssignees) {
                if (!userRepository.existsById(assignee.getId())) {
                    throw new ResourceNotFoundException("Assignee with ID " + assignee.getId() + " does not exist");
                }
            }

            // comparing existing assignees with new assignees
            Set<UserEntity> addedAssignees = new HashSet<>(newAssignees);
            Set<UserEntity> removedAssignees = new HashSet<>(oldAssignees);

            //added assignees
            addedAssignees.removeAll(oldAssignees);
            // removed assignees
            removedAssignees.removeAll(newAssignees);

            // add new assignees
            if (!addedAssignees.isEmpty()) {
                existingTask.getAssignees().addAll(addedAssignees);
                addedAssignees.forEach(assignee -> {
                    notificationService.addNotification("You have been assigned to the task: " + existingTask.getName(), assignee);
                });
                isUpdated = true;
            }

            // delete assignees
            if (!removedAssignees.isEmpty()) {
                existingTask.getAssignees().removeAll(removedAssignees);
                removedAssignees.forEach(assignee -> {
                    notificationService.addNotification("You have been removed from the task: " + existingTask.getName(), assignee);
                });
                isUpdated = true;
            }
        }

        if (updatedTask.getDueDate() != null &&
                (existingTask.getDueDate() == null || !existingTask.getDueDate().equals(updatedTask.getDueDate()))) {
            existingTask.setDueDate(updatedTask.getDueDate());
            isUpdated = true;
        }


        if (isUpdated) {
            taskRepository.save(existingTask);
            notificationService.addNotification("The task: " + existingTask.getName() + " has been updated!", existingTask.getProject().getOwner());
        }

        return new ActionSuccessResponse(ownerEmail, "Task updated successfully! " + existingTask.getId());
    }

    @Override
    @Transactional
    public ActionSuccessResponse updateTaskStatus(Long taskId, Long updatedTaskStatusId) {
        String userEmail = tokenService.getEmail();

        TaskEntity existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));


        boolean isAssigned = existingTask.getAssignees().stream()
                .anyMatch(assignee -> assignee.getEmail().equals(userEmail));

        if (!isAssigned) {
            throw new InvalidUserException("You are not assigned to this task");
        }

        TaskStatusEntity updatedStatus = taskStatusRepository.findById(updatedTaskStatusId)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found"));

        existingTask.setStatus(updatedStatus);

        taskRepository.save(existingTask);

        UserEntity user = userRepository.findByEmail(userEmail).orElseThrow(() -> new InvalidUserException("User not found!"));

        notificationService.addNotification("Task status updated", user);

        return new ActionSuccessResponse(userEmail, "Task status updated successfully!");
    }

    @Override
    public ActionSuccessResponse getTasksProgress() {
        String userEmail = tokenService.getEmail();

        List<TaskEntity> tasks = taskRepository.findByAssigneesEmail(userEmail);

        long completedTasks = tasks.stream()
                .filter(task -> task.getStatus().getName().equalsIgnoreCase("Completed"))
                .count();

        long pendingTasks = tasks.stream()
                .filter(task -> !task.getStatus().getName().equalsIgnoreCase("Completed"))
                .count();

        return new ActionSuccessResponse(userEmail, "Progress Overview: " +
                "Completed: " + completedTasks + ", Pending: " + pendingTasks);
    }



    @Override
    public List<TaskDetailsDTO> getTasksForCurrentUser() {
        String userEmail = tokenService.getEmail();

        // Find all tasks where the user is an assignee
        List<TaskEntity> tasks = taskRepository.findByAssigneesEmail(userEmail);

        return tasks.stream().map(task -> {
            TaskDetailsDTO taskDetail = new TaskDetailsDTO();
            taskDetail.setId(task.getId());
            taskDetail.setName(task.getName());
            taskDetail.setStatus(task.getStatus().getName());

            List<String> assigneeEmails = task.getAssignees().stream()
                    .filter(assignee -> assignee.getEmail() != null)
                    .map(assignee -> assignee.getEmail())
                    .collect(Collectors.toList()); // Collect all the emails into a list

            taskDetail.setAssignedUsers(assigneeEmails); // Set the list of assignees' emails

            taskDetail.setTaskOwner(task.getProject().getOwner().getEmail());
            taskDetail.setProjectName(task.getProject().getName());
            taskDetail.setDueDate(task.getDueDate());

            return taskDetail;
        }).collect(Collectors.toList());
    }

}
