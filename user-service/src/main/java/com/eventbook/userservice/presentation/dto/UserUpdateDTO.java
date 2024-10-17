package com.eventbook.userservice.presentation.dto;

public class UserUpdateDTO {
    private boolean admin;
    private String username;
    private String email;


    public UserUpdateDTO() {}


    public UserUpdateDTO(String username, String email, boolean admin) {
        this.username = username;
        this.email = email;
        this.admin = admin;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}