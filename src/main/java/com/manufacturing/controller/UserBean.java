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
import java.util.List;

@Component
@ViewScoped
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private UserService userService;

    private List<User> users;
    private User selectedUser;
    private User user;

    @PostConstruct
    public void init() {
        loadUsers();
        user = new User();
    }

    public void loadUsers() {
        users = userService.findAll();
    }

    public void openNew() {
        user = new User();
        user.setActive(true);
        user.setRole("VIEWER");
        user.setCreatedDate(LocalDateTime.now());
    }

    public void saveUser() {
        try {
            // Validate
            if (user.getId() == null && userService.usernameExists(user.getUsername())) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Username already exists"));
                return;
            }

            if (user.getId() == null && userService.emailExists(user.getEmail())) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Email already exists"));
                return;
            }

            userService.save(user);
            loadUsers();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                            "User saved successfully"));
            user = new User();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Failed to save user: " + e.getMessage()));
        }
    }

    public void deleteUser() {
        try {
            userService.delete(selectedUser.getId());
            users.remove(selectedUser);
            selectedUser = null;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                            "User deleted successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Failed to delete user"));
        }
    }

    // Getters and Setters
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}