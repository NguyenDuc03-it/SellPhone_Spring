package com.example.SellPhone.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String redirectUrl = request.getParameter("redirect");
        if (redirectUrl == null) {
            redirectUrl = "/";
        }

        // Thêm error=true và giữ nguyên redirect để quay lại trang cũ khi đăng nhập thành công lần sau
        String failureUrl = "/login?error=true&redirect=" + URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8);

        response.sendRedirect(failureUrl);
    }
}
