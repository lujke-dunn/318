package com.eventbook.userservice.presentation.dto;

public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private boolean isAdmin;

    // Default constructor
    public UserResponseDTO() {}

    // Constructor with all fields
    public UserResponseDTO(Long id, String username, String email, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;

    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isAdmin() {
        return isAdmin;
    }
}