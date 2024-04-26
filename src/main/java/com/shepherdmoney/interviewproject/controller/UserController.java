package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PutMapping("/user")
    public ResponseEntity<Integer> createUser(@RequestBody CreateUserPayload payload) {
        // Instantiate a new User object.
        User newUser = new User();
        // Set the user's name and email from the request payload.
        newUser.setName(payload.getName());
        newUser.setEmail(payload.getEmail());
        // Call the userRepository to save the new user to the database.
        User savedUser = userRepository.save(newUser);
        // Return the newly created user's ID in the response body with an HTTP 200 OK status.
        return ResponseEntity.ok(savedUser.getId());
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@RequestParam int userId) {
        // Attempt to find the user by their ID in the repository.
        Optional<User> userOptional = userRepository.findById(userId);
        // Check if the user exists in the database.
        if (userOptional.isPresent()) {
            // If the user is found, delete the user from the repository.
            userRepository.delete(userOptional.get());
            // Return an HTTP 200 OK response with a success message.
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            // If no user is found with the given ID, return a 400 Bad Request response with an error message.
            return ResponseEntity.badRequest().body("User not found.");
        }
    }
}
