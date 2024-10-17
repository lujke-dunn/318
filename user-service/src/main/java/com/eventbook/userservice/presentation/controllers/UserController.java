package com.eventbook.userservice.presentation.controllers;



import com.eventbook.userservice.domain.models.User;
import com.eventbook.userservice.presentation.dto.UserCreateDTO;
import com.eventbook.userservice.presentation.dto.UserResponseDTO;
import com.eventbook.userservice.presentation.dto.UserUpdateDTO;
import com.eventbook.userservice.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        User user = new User(userCreateDTO.getUsername(), userCreateDTO.getEmail(), userCreateDTO.getIsAdmin());
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(convertToDTO(createdUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        return user != null ? ResponseEntity.ok(convertToDTO(user)) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO, @RequestHeader("User-Id") Long requestUserId) {
        return userService.updateUser(id, userUpdateDTO, requestUserId);
    }

    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.isAdmin());
    }


}