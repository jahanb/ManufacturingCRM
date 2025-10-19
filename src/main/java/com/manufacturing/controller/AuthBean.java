package com.manufacturing.controller;

import com.manufacturing.model.User;
import com.manufacturing.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;

@Component
@ViewScoped
public class AuthBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private UserService userService;

    private String username;
    private String password;
    private boolean rememberMe;

    private User currentUser;

    @PostConstruct
    public void init() {
        checkExistingSession();
    }

    private void checkExistingSession() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext != null && facesContext.getExternalContext() != null) {
                HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);

                if (session != null) {
                    User sessionUser = (User) session.getAttribute("currentUser");
                    if (sessionUser != null) {
                        currentUser = sessionUser;
                        System.out.println("Session found for user: " + currentUser.getUsername());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking session: " + e.getMessage());
        }
    }

    public void login() {
        try {
            if (username == null || username.trim().isEmpty() ||
                    password == null || password.trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Please enter username and password"));
                return;
            }

            System.out.println("Attempting login for username: " + username);

            if (userService.authenticate(username, password)) {
                Optional<User> userOpt = userService.findByUsername(username);
                if (userOpt.isPresent()) {
                    currentUser = userOpt.get();

                    System.out.println("User authenticated: " + currentUser.getUsername());

                    // Get FacesContext and ExternalContext
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    ExternalContext externalContext = facesContext.getExternalContext();

                    // Create new session
                    HttpSession session = (HttpSession) externalContext.getSession(true);

                    // Store user in session
                    session.setAttribute("currentUser", currentUser);
                    session.setMaxInactiveInterval(rememberMe ? 86400 : 1800);

                    System.out.println("Session ID: " + session.getId());
                    System.out.println("Session created with user: " + currentUser.getUsername());

                    // Get request to verify session
                    HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
                    System.out.println("Request session ID: " + request.getSession().getId());

                    // Verify session was set
                    Object storedUser = session.getAttribute("currentUser");
                    if (storedUser != null) {
                        System.out.println("Verified: User stored in session successfully");
                    } else {
                        System.err.println("ERROR: User NOT stored in session!");
                    }

                    // Add flash message
                    externalContext.getFlash().setKeepMessages(true);
                    facesContext.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                                    "Welcome back, " + currentUser.getFirstName() + "!"));

                    // Redirect to index
                    String contextPath = externalContext.getRequestContextPath();
                    System.out.println("Redirecting to: " + contextPath + "/index.xhtml");
                    externalContext.redirect(contextPath + "/index.xhtml");
                    facesContext.responseComplete();
                    return;
                }
            }

            System.out.println("Authentication failed for username: " + username);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Invalid username or password"));

        } catch (IOException e) {
            System.err.println("Redirect error: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Login failed: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Login failed: " + e.getMessage()));
        }
    }

    public void logout() {
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                    .getExternalContext().getSession(false);

            if (session != null) {
                System.out.println("Invalidating session for user: " + (currentUser != null ? currentUser.getUsername() : "unknown"));
                session.invalidate();
            }

            currentUser = null;
            username = null;
            password = null;

            // Redirect to login
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            String contextPath = externalContext.getRequestContextPath();
            externalContext.redirect(contextPath + "/login.xhtml");
            facesContext.responseComplete();

        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isLoggedIn() {
        if (currentUser == null) {
            checkExistingSession();
        }
        return currentUser != null;
    }

    public boolean hasRole(String role) {
        return currentUser != null && currentUser.getRole() != null && currentUser.getRole().equals(role);
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public User getCurrentUser() {
        if (currentUser == null) {
            checkExistingSession();
        }
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
