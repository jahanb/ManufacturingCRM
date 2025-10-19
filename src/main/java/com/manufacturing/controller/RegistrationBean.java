package com.manufacturing.controller;

import com.manufacturing.model.User;
import com.manufacturing.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;

@Component
@ViewScoped
public class RegistrationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private UserService userService;

    private User user;
    private String confirmPassword;

    @PostConstruct
    public void init() {
        user = new User();
    }

    public String register() {
        try {
            // Validate password confirmation
            if (!user.getPassword().equals(confirmPassword)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Passwords do not match"));
                return null;
            }

            // Check if username already exists
            if (userService.usernameExists(user.getUsername())) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Username already exists. Please choose another."));
                return null;
            }

            // Check if email already exists
            if (userService.emailExists(user.getEmail())) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Email already registered. Please use another email."));
                return null;
            }

            // Set default values
            user.setRole("VIEWER"); // Read-only access
            user.setActive(true);
            user.setCreatedDate(LocalDateTime.now());

            // Save user (password will be encrypted in UserService)
            userService.save(user);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                            "Account created successfully! You can now login with VIEWER access."));

            // Clear form
            user = new User();
            confirmPassword = null;

            // Redirect to login after 2 seconds
            FacesContext.getCurrentInstance().getExternalContext()
                    .getFlash().setKeepMessages(true);

            return "/login.xhtml?faces-redirect=true";

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Failed to create account: " + e.getMessage()));
            return null;
        }
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}