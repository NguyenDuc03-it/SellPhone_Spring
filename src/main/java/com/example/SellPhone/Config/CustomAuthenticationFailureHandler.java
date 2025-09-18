package com.example.SellPhone.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String errorMessage;

        if (exception instanceof BadCredentialsException) {
            errorMessage = "Tài khoản hoặc mật khẩu không chính xác. Vui lòng thử lại!";
        } else if (exception instanceof DisabledException) {
            errorMessage = "Tài khoản này đã bị khóa. Liên hệ CSKH để biết thêm chi tiết.";
        } else {
            errorMessage = "Đăng nhập thất bại. Vui lòng thử lại!";
        }


        String redirectUrl = request.getParameter("redirect");
        if (redirectUrl == null) {
            redirectUrl = "/";
        }

        // Thêm error=true và giữ nguyên redirect để quay lại trang cũ khi đăng nhập thành công lần sau
        String failureUrl = "/login?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8) + "&redirect=" + URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8);

        response.sendRedirect(failureUrl);
    }
}
