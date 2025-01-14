package javaweb.task_management_system.services;


import org.springframework.security.core.Authentication;

public interface TokenService {
     String generateJwt(Authentication auth);
     String getEmail();
}
