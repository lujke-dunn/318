package com.eventbook.userservice.service;

import com.eventbook.userservice.domain.models.User;
import com.eventbook.userservice.domain.models.UserDomainService;
import com.eventbook.userservice.infrastructure.UserRepository;
import com.eventbook.userservice.presentation.dto.UserResponseDTO;
import com.eventbook.userservice.presentation.dto.UserUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDomainService userDomainService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String USER_CREATED_TOPIC = "user-created-topic";

    public UserService(UserRepository userRepository, UserDomainService userDomainService, KafkaTemplate<String, String> kafkaTemplate) {
        this.userRepository = userRepository;
        this.userDomainService = userDomainService;
        this.kafkaTemplate = kafkaTemplate;
    }

    public User createUser(User user) {
        userDomainService.validateUser(user);
        User createdUser = userRepository.save(user);
        
        // Send message to Kafka for notification
        String message = String.format("{\"userId\":%d,\"username\":\"%s\",\"email\":\"%s\"}",
            createdUser.getId(), createdUser.getUsername(), createdUser.getEmail());
        kafkaTemplate.send(USER_CREATED_TOPIC, message);
        
        return createdUser;
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }


    public ResponseEntity<?> updateUser(Long userId, UserUpdateDTO userUpdateDTO, Long requestUserId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();
        User requestUser = userRepository.findById(requestUserId).orElse(null);

        if (requestUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requesting user not found");
        }

        // Check if the requesting user is the same as the user being updated or an admin
        if (!userId.equals(requestUserId) && !requestUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to update this user");
        }

        // Update user fields
        if (userUpdateDTO.getUsername() != null) {
            user.setUsername(userUpdateDTO.getUsername());
        }
        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }

        // Handle admin status update
        if (userUpdateDTO.isAdmin() != null) {
            if (requestUser.isAdmin()) {
                user.setAdmin(userUpdateDTO.isAdmin());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only administrators can change admin status");
            }
        }

        // Validate updated user
        try {
            userDomainService.validateUser(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(convertToDTO(updatedUser)).status(HttpStatus.CREATED).build();
    }

    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.isAdmin());
    }

}
