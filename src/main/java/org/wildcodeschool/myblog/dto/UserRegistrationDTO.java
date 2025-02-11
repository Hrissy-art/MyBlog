package org.wildcodeschool.myblog.dto;

public class UserRegistrationDTO {
    private String email;
    private String password;

    public UserRegistrationDTO() {
    }

    // Constructeur avec paramètres
    public UserRegistrationDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Optionnel : méthode toString() pour afficher les valeurs de l'objet
    @Override
    public String toString() {
        return "UserRegistrationDTO{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}