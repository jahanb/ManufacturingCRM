package com.manufacturing.service;

import com.manufacturing.model.User;
import com.manufacturing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User save(User user) {
        // Encrypt password if it's a new user or password is being changed
        if (user.getId() == null || (user.getPassword() != null && !user.getPassword().startsWith("$2a$"))) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getCreatedDate() == null) {
            user.setCreatedDate(LocalDateTime.now());
        }

        return userRepository.save(user);
    }

    public void delete(String id) {
        userRepository.deleteById(id);
    }

    public boolean authenticate(String username, String rawPassword) {
        try {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.println("User found: " + user.getUsername() + ", Active: " + user.isActive());

                if (user.isActive() && passwordEncoder.matches(rawPassword, user.getPassword())) {
                    // Update last login
                    user.setLastLogin(LocalDateTime.now());
                    userRepository.save(user);
                    System.out.println("Authentication successful for: " + username);
                    return true;
                } else {
                    System.out.println("Password mismatch or user inactive for: " + username);
                }
            } else {
                System.out.println("User not found: " + username);
            }
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}