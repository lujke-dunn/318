package com.eventbook.userservice.presentation.dto;

public class UserCreateDTO {
    private String username;
    private String email;
    private Boolean isAdmin;

    // Default constructor
    public UserCreateDTO() {}

    // Constructor with all fields
    public UserCreateDTO(String username, String email, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
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

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}