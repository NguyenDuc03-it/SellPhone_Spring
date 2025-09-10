package com.example.SellPhone.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


import java.io.IOException;


public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication) throws IOException, ServletException {
//
//        System.out.println("User " + authentication.getName() + " has logged in successfully.");
//        String targetUrl = determineTargetUrl(authentication);
//
//        if (response.isCommitted()) {
//            return;
//        }
//
//        response.sendRedirect(targetUrl);
//    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String redirectUrl = request.getParameter("redirect");

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        boolean isEmployee = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_EMPLOYEE"));

        boolean isCustomer = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CUSTOMER"));

        // Nếu là khách hàng và có redirect, chuyển về trang trước
        if (isCustomer && redirectUrl != null && redirectUrl.startsWith("/")) {
            response.sendRedirect(redirectUrl);
        }
        // Nếu là admin hoặc nhân viên, chuyển đến trang quản trị
        else if (isAdmin || isEmployee) {
            response.sendRedirect("/management/dashboard");
        }
        // Nếu không rõ vai trò hoặc không có redirect, đưa về home
        else {
            response.sendRedirect("/");
        }
    }

    protected String determineTargetUrl(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CUSTOMER"));

        if (isAdmin) {
            return "/management/dashboard"; // Trang quản trị cho admin
        } else if (isUser) {
            return "/user/home"; // Trang người dùng cho user
        } else {
            throw new IllegalStateException("Không thể xác định vai trò của người dùng");
        }
    }
}