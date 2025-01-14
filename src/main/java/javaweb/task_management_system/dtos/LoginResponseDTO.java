package javaweb.task_management_system.dtos;


public class LoginResponseDTO {

    private UserDTO user;
    private String jwt;


    public LoginResponseDTO(){
        super();
    }

    public LoginResponseDTO(UserDTO user, String jwt){
        this.user = user;
        this.jwt = jwt;
    }

    public UserDTO getUser(){
        return this.user;
    }


    public String getJwt(){
        return this.jwt;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
