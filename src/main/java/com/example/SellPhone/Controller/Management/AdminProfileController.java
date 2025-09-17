package com.example.SellPhone.Controller.Management;

import com.example.SellPhone.Config.CustomUserDetails;
import com.example.SellPhone.DTO.Request.User.UserProfileUpdateRequest;
import com.example.SellPhone.Entity.User;
import com.example.SellPhone.Service.StaffService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Controller
@RequestMapping("/management/profile")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminProfileController {

    StaffService staffService;
    PasswordEncoder passwordEncoder;

    @GetMapping
    public String viewProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login?redirect=/user/profile"; // Chuyển hướng đến trang đăng nhập nếu chưa đăng nhập
        }
        Long userId = ((CustomUserDetails) ((Authentication) principal).getPrincipal()).getUserId();

        User user = staffService.getStaffById(userId);
        String dob = user.getDob(); // Lấy ngày sinh dạng dd/MM/yyyy
        String formattedDob = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(dob);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            formattedDob = outputFormat.format(date); // Chuyển đổi thành yyyy-MM-dd
        } catch (ParseException e) {
            e.printStackTrace(); // Xử lý lỗi nếu có
        }

        // Cập nhật ngày sinh với định dạng đúng
        user.setDob(formattedDob);
        model.addAttribute("user", user);
        return "DashBoard/admin-profile";
    }

    @PostMapping("/update-info")
    public String updateProfile(@ModelAttribute("user") @Valid UserProfileUpdateRequest request,
                                BindingResult result,
                                Principal principal,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login?redirect=/management/profile";
        }

        Long userId = ((CustomUserDetails) ((Authentication) principal).getPrincipal()).getUserId();

        if (result.hasErrors()) {
            model.addAttribute("user", request);
            return "DashBoard/admin-profile"; // Load lại trang với lỗi
        }

        // Đảm bảo userId trong form là chính chủ
        if (!request.getUserId().equals(userId)) {
            model.addAttribute("errorMessage", "Chỉ có thể sửa thông tin của chính bạn");
            return "DashBoard/admin-profile"; // Load lại trang với lỗi
        }

        // Kiểm tra ngày sinh có phải trong tương lai không
        if (isDobInFuture(request.getDob())) {
            model.addAttribute("errorMessage", "Ngày sinh không thể là ngày trong tương lai");
            return "DashBoard/admin-profile"; // Trả về thông báo lỗi nếu ngày sinh không hợp lệ
        }

        if(staffService.doesCustomerExistByCCCD(request.getCCCD()) && !request.getCCCD().equals(staffService.getStaffById(request.getUserId()).getCCCD())){
            model.addAttribute("errorMessage", "CCCD đã tồn tại");
            return "Sell/profile"; // Trả về thông báo lỗi nếu CCCD đã tồn tại
        }

        try {
            staffService.updateInfo(request); // Gọi service để cập nhật

            User user = staffService.getStaffById(userId);
            String dob = user.getDob(); // Lấy ngày sinh dạng dd/MM/yyyy
            String formattedDob = null;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date date = sdf.parse(dob);
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                formattedDob = outputFormat.format(date); // Chuyển đổi thành yyyy-MM-dd
            } catch (ParseException e) {
                e.printStackTrace(); // Xử lý lỗi nếu có
            }

            // Cập nhật ngày sinh với định dạng đúng
            user.setDob(formattedDob);
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công.");
            return "redirect:/management/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/management/profile"; // Chuyển hướng về trang profile
        }
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        Long userId = ((CustomUserDetails) ((Authentication) principal).getPrincipal()).getUserId();
        User user = staffService.getStaffById(userId);

        // So sánh mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("currentPasswordSave", currentPassword);
            redirectAttributes.addFlashAttribute("newPasswordSave", newPassword);
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu hiện tại không đúng");
            return "redirect:/management/profile?section=password";
        }

        if(currentPassword.equals(newPassword)){
            redirectAttributes.addFlashAttribute("currentPasswordSave", currentPassword);
            redirectAttributes.addFlashAttribute("newPasswordSave", newPassword);
            redirectAttributes.addFlashAttribute("newPasswordMessage", "Mật khẩu mới không được trùng với mật khẩu hiện tại");
            return "redirect:/management/profile?section=password";
        }

        try {
            staffService.changePassword(userId, newPassword);

            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công");
            return "redirect:/management/profile?section=password";
        }
        catch(Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/management/profile?section=password";
        }
    }

    // Phương thức kiểm tra ngày sinh có phải là trong tương lai không
    private boolean isDobInFuture(String dob) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Giả sử định dạng là yyyy-MM-dd
        try {
            LocalDate dobDate = LocalDate.parse(dob, formatter);
            return dobDate.isAfter(LocalDate.now()); // Kiểm tra xem ngày sinh có phải sau ngày hiện tại không
        } catch (Exception e) {
            return false; // Nếu không thể parse ngày sinh, coi như không hợp lệ
        }
    }

}
