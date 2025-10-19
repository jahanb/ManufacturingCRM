package com.manufacturing.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AuthFilter implements Filter {

    public void init(FilterConfig filterConfig) {
        System.out.println("=========================================");
        System.out.println("AuthFilter initialized");
        System.out.println("=========================================");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String requestURI = req.getRequestURI();

        System.out.println("🔍 AuthFilter - Request URI: " + requestURI);

        // Allow access to login page, register page, and resources
        if (requestURI.contains("/login.xhtml") ||
                requestURI.contains("/register.xhtml") ||
                requestURI.contains("/jakarta.faces.resource/")) {
            System.out.println("✅ AuthFilter - Allowing public access to: " + requestURI);
            chain.doFilter(request, response);
            return;
        }

        // Check if user is logged in
        if (session == null) {
            System.out.println("❌ AuthFilter - No session found, redirecting to login");
            res.sendRedirect(req.getContextPath() + "/login.xhtml");
            return;
        }

        Object currentUser = session.getAttribute("currentUser");
        if (currentUser == null) {
            System.out.println("❌ AuthFilter - No user in session (Session ID: " + session.getId() + "), redirecting to login");
            res.sendRedirect(req.getContextPath() + "/login.xhtml");
            return;
        }

        System.out.println("✅ AuthFilter - User authenticated, allowing access to: " + requestURI);

        // User is logged in, proceed
        chain.doFilter(request, response);
    }

    public void destroy() {
        System.out.println("AuthFilter destroyed");
    }
}