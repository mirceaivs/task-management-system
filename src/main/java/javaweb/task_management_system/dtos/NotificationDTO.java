package javaweb.task_management_system.dtos;


import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;
    private UserDTO recipient;

    public NotificationDTO(Long id, String content, boolean isRead, LocalDateTime createdAt, UserDTO recipient) {
        this.id = id;
        this.content = content;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.recipient = recipient;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO getRecipient() {
        return recipient;
    }

    public void setRecipient(UserDTO recipient) {
        this.recipient = recipient;
    }
}
