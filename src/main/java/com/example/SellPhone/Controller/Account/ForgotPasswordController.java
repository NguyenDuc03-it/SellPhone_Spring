package com.example.SellPhone.Controller.Account;

import com.example.SellPhone.Service.CustomerService;
import com.example.SellPhone.Service.EmailService;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.SecureRandom;

@Controller
@RequiredArgsConstructor
@RequestMapping("/forgot-password")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ForgotPasswordController {

    EmailService emailService;
    CustomerService customerService;
    static SecureRandom secureRandom = new SecureRandom();

    // Gửi mail OTP
    @PostMapping("/send-email")
    public String sendForgotPasswordEmail(@RequestParam("email") String email, RedirectAttributes redirectAttributes, HttpSession session, Model model) {

        if(email == null || email.isEmpty()) {
            model.addAttribute("errorMessage", "Không nhận được email. Vui lòng thử lại!");
            return "AboutAccount/forgot-password";
        }
        boolean isEmailExists = customerService.doesCustomerExistByEmail(email);

        if(!isEmailExists){
            model.addAttribute("errorMessage", "Email không tồn tại trong hệ thống!");
            return "AboutAccount/forgot-password";
        }

        String otp = generateRandomOTP();
        System.out.println("Mã OTP: " + otp);

        session.setAttribute("otp", otp);
        session.setAttribute("otpEmail", email);
        session.setAttribute("otpTime", System.currentTimeMillis());

        try {
            emailService.sendConfirmationEmail(email, "Mã OTP xác nhận quên mật khẩu", otp);
            redirectAttributes.addFlashAttribute("successMessage", "Mã OTP đã được gửi đến email của bạn!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã có lỗi xảy ra khi gửi mã OTP.");
        }
        // Logic to send forgot password email
        return "redirect:/forgot-password/send-email/otp";
    }

    // Hiển thị trang nhập OTP
    @GetMapping("/send-email/otp")
    public String showOtpVerificationPage(Model model) {
        return "AboutAccount/otp-confirm";
    }

    // Xác thực OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") String otp, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        // Lấy mã OTP đã lưu trong session
        String correctOtp = (String) session.getAttribute("otp");
        Long createdTime = (Long) session.getAttribute("otpTime");
        long now = System.currentTimeMillis();

        if (otp == null || !otp.equals(correctOtp)) {
            model.addAttribute("errorMessage", "Mã OTP không chính xác. Vui lòng thử lại.");
            return "AboutAccount/otp-confirm";
        }

        if (createdTime == null || (now - createdTime > 5 * 60 * 1000)) { // quá 5 phút
            model.addAttribute("errorMessage", "Mã OTP đã hết hạn.");
            return "AboutAccount/otp-confirm";
        }

        // OTP chính xác, chuyển đến trang đổi mật khẩu
        session.setAttribute("correctOtp", true);
        session.removeAttribute("otpTime"); // Xóa otpTime khỏi session sau khi đã xác thực thành công
        return "redirect:/forgot-password/reset-password";  // Trang đổi mật khẩu
    }

    // Gửi lại OTP
    @PostMapping("/send-mail/resend-otp")
    public String resendOtp(HttpSession session, RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("otpEmail");
        if(email == null || email.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không nhận được email. Vui lòng thử lại!");
            return "redirect:/forgot-password/send-email/otp";
        }
        String otp = generateRandomOTP();
        System.out.println("Mã OTP: " + otp);

        session.setAttribute("otp", otp);

        try {
            emailService.sendConfirmationEmail(email, "Mã OTP xác nhận quên mật khẩu", otp);
            redirectAttributes.addFlashAttribute("successMessage", "Mã OTP đã được gửi đến email của bạn!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã có lỗi xảy ra khi gửi mã OTP.");
        }
        return "redirect:/forgot-password/send-email/otp";
    }

    // Hiển thị trang đổi mật khẩu
    @GetMapping("/reset-password")
    public String showResetPassPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("correctOtp") == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần xác thực OTP trước khi đổi mật khẩu.");
            return "redirect:/forgot-password";
        }
        return "AboutAccount/reset-pass";
    }

    // Xử lý đổi mật khẩu
    @PostMapping("/reset-password/change")
    public String changePassword(@RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        Boolean correctOtp = (Boolean) session.getAttribute("correctOtp");
        if (correctOtp == null || !correctOtp) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần xác thực OTP trước khi đổi mật khẩu.");
            return "redirect:/forgot-password";
        }

        String email = (String) session.getAttribute("otpEmail");
        session.removeAttribute("correctOtp");

        if(email == null || email.isEmpty()) {
            model.addAttribute("errorMessage", "Không nhận được email. Vui lòng thử lại!");
            return "AboutAccount/reset-pass";
        }
        if(newPassword == null || newPassword.isEmpty()) {
            model.addAttribute("errorMessage", "Mật khẩu mới không được để trống!");
            return "AboutAccount/reset-pass";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
            return "AboutAccount/reset-pass";
        }

        try {
            boolean isUpdated = customerService.updateCustomerPasswordByEmail(email, newPassword);
            if(isUpdated) {
                // Xóa các thuộc tính liên quan đến OTP khỏi session
                session.removeAttribute("otp");
                session.removeAttribute("otpEmail");

                redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công! Bạn có thể đăng nhập ngay bây giờ.");
                return "redirect:/login";  // Trang đăng nhập
            } else {
                model.addAttribute("errorMessage", "Đã có lỗi xảy ra khi đổi mật khẩu. Vui lòng thử lại.");
                return "AboutAccount/reset-pass";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "AboutAccount/reset-pass";
        }
    }

    // Hàm để tạo mã OTP ngẫu nhiên
    private String generateRandomOTP() {
        int otp = 100000 + secureRandom.nextInt(900000);  // Generate a 6-digit number
        return String.valueOf(otp);
    }

}
