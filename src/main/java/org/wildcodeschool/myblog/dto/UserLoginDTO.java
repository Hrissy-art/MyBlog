package org.wildcodeschool.myblog.dto;

public class UserLoginDTO {
    private String email;
    private String password;

    // Constructeur par défaut
    public UserLoginDTO() {}

    // Constructeur avec paramètres
    public UserLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserLoginDTO{" +
                "email='" + email + '\'' +
                ", password='******'}";
    }
}
