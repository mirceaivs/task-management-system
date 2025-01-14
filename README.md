# Task Management System - Proiect Programare Web Java

This project is a **Task Management System** developed using **Java** and **Spring Boot**. It leverages **Docker** to set up a MySQL database and provides a basic structure to manage tasks, users, projects, and notifications. The application uses **RSA-encrypted JWT tokens** for user authentication.

## 📝 Business Requirements

### Task Management System - 10 Business Requirements

1. **User Registration and Authentication**  
   Users must register and log in securely to access the system. Upon successful login, users will be authenticated with **RSA-encrypted JWT tokens** stored in cookies. 
 
   **Objective:** Ensure only authorized users can access and manage tasks.

2. **Task Management**  
   Project Owners will be able to create, update, and delete tasks with details like title, description, due date, and status.  

   **Objective:** Provide the ability to efficiently manage tasks for a project.

3. **Project Management**  
   Project Owners can create new projects, which serve as containers for tasks. They will be able to update project details and delete projects if necessary.  

   **Objective:** Organize tasks under projects to structure the work.

4. **Notification System**  
   Each action (e.g., task creation, update, assignment, deletion) will generate a notification to the user. Notifications will be available for users to view.  

   **Objective:** Keep users informed about key actions related to their tasks and projects.

5. **Task Status Tracking**  
   Tasks will have a status that can be updated over time. Status options will include:
   - `To Do`
   - `In Progress`
   - `Completed`  

   **Objective:** Help users track task progress and stay updated on the status of tasks assigned to them.

6. **Task Assignment**  
   Project Owners can assign tasks to specific team members. This allows for task delegation and ensures accountability for each task.  

   **Objective:** Ensure tasks are completed by the right individuals and promote collaboration.

7. **Role-Based Access Control**  
   The system will have three roles:  
   - **Admin:** Full access to manage users, tasks, projects, etc.  
   - **Project Owner:** Responsible for creating projects, tasks, and assigning tasks.  
   - **User:** Regular users who can view and work on tasks assigned to them.  

   **Objective:** Enforce proper permissions and ensure that users can only perform actions that they are authorized to.

8. **Progress Overview**  
   Users will have an overview of their tasks, with the ability to see a summary of completed vs. pending tasks. This allows users to track their productivity and focus on tasks that are in progress or pending.  

   **Objective:** Help users prioritize their work and track overall progress.

9. **Data Security and Privacy**  
   Authentication will use **RSA-encrypted JWT tokens** stored in cookies. Only authorized users can access task data, and sensitive information will always be protected.  

   **Objective:** Ensure that user data and tasks are secure and protected from unauthorized access.

10. **Task View and Filter**  
    Users will have the ability to filter tasks by different criteria such as project, status (e.g., To Do, In Progress, Completed), or due date.  

    **Objective:** Improve task visibility and organization by enabling users to filter tasks based on their needs.

## 🚀 MVP Features

### 1. User Authentication & Registration System

**Description:**  
The system will allow users to register, log in, and authenticate securely using **RSA-encrypted JWT tokens** stored in cookies. This is essential to ensure the integrity of the system and protect user data.

**Key Features:**

- **User Registration:** Users can create an account with essential details (email, password, role).
- **User Login:** Users can log in with their credentials. Upon successful login, they will receive an RSA-encrypted JWT token for secure session management.
- **Session Management:** JWT tokens will be used to manage user sessions securely.

---

### 2. Task Management and Status Tracking

**Description:**  
In the MVP phase, users will be able to create, update, delete, and track tasks. Each task will have a status (e.g., "To Do", "In Progress", "Completed") to allow users to monitor progress.

**Key Features:**

- **Create Tasks:** Users can create tasks with a title, description, due date, and status.
- **Update Task Status:** Project Owners can update the status of tasks.
- **Task Deletion:** Tasks can be deleted when no longer needed.
- **View Task Progress:** Users can view the progress of tasks assigned to them.

---

### 3. Project Management and Task Assignment

**Description:**  
The system will allow Project Owners to create projects, add tasks under projects, and assign tasks to specific team members. This ensures tasks are organized under projects and users are accountable for completing their tasks.

**Key Features:**

- **Create Projects:** Project Owners can create projects to group related tasks.
- **Assign Tasks:** Project Owners can assign specific tasks to team members.
- **Manage Tasks within Projects:** Users can view tasks related to specific projects.

---

### 4. Notification System

**Description:**  
The notification system will inform users about important actions performed within the system (task assignments, status changes, etc.). Notifications will be sent to users whenever an action involving them is performed.

**Key Features:**

- **Send Notifications:** Notifications will be sent to users when tasks are assigned, statuses change, or new tasks/projects are created.
- **Mark as Read:** Users can mark notifications as read once they've seen them.
- **Delete Notifications:** Users can delete notifications if no longer needed.

---

### 5. Task View and Filtering

**Description:**  
The system will allow users to filter tasks based on status, due date, or project. This will help users focus on tasks that need immediate attention.

**Key Features:**

- **View Tasks:** Users can view tasks assigned to them, grouped by project or status.
- **Filter by Status:** Users can filter tasks by status (e.g., "To Do", "In Progress", "Completed").
- **Filter by Project and Due Date:** Users can filter tasks based on project and due date to stay on top of deadlines.


---

# Installation & Setup

Follow the steps below to set up the **Task Management System**.

---

### 1. Clone the repository

Clone the project repository to your local machine:

```bash
git clone https://github.com/your-username/task-management-system.git
cd task-management-system
```

### 2. Docker Setup for MySQL Database
```bash
docker run --hostname=9af1a12c48aa \
--mac-address=02:42:ac:11:00:02 \
--env=MYSQL_ROOT_PASSWORD=root \
--env=MYSQL_DATABASE=task_management \
--env=PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin \
--env=GOSU_VERSION=1.17 \
--env=MYSQL_MAJOR=innovation \
--env=MYSQL_VERSION=9.1.0-1.el9 \
--env=MYSQL_SHELL_VERSION=9.1.0-1.el9 \
--volume=/var/lib/mysql \
--network=bridge \
--workdir=/ \
-p 4306:3306 \
--restart=no \
--label='desktop.docker.io/wsl-distro=Ubuntu' \
--runtime=runc \
-d mysql:latest

```
### 3. Build and run the application

```bash
mvn spring-boot:run
```
### 4. Open Swagger UI to interact with the API.

```bash
http://localhost:8080/swagger-ui/index.html
```
