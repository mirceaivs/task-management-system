package javaweb.task_management_system.dtos;



import javaweb.task_management_system.models.RoleEntity;
import javaweb.task_management_system.models.UserEntity;

import java.util.*;

public class UserDTO {
    private final Long id;
    private final String name;
    private final String lastname;
    private final String email;
//    private final Set<RoleEntity> roles;
    private final List<RoleEntity> roles;

    public UserDTO(UserEntity user) {
        this.id = user.getId();
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.roles = new ArrayList<>(user.getRoles());
        this.email = user.getEmail();

    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail(){
        return email;
    }

    public List<RoleEntity> getRoles() {
        return roles;
    }



}