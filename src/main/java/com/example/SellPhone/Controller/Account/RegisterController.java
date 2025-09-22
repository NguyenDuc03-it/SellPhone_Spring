package com.example.SellPhone.Controller.Account;

import com.example.SellPhone.DTO.Request.User.UserCreationRequest;
import com.example.SellPhone.Service.CustomerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
@RequestMapping("/register")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegisterController {

    CustomerService customerService;

    @PostMapping("/create-account")
    public String createAccount(@Valid @ModelAttribute("customer") UserCreationRequest request, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes
                                    , HttpSession session) {
        if (bindingResult.hasErrors()) {
            // Xử lý lỗi
            model.addAttribute("customer", request);
            return "AboutAccount/register"; // Trả về một thông báo lỗi
        }

        // Kiểm tra ngày sinh có phải trong tương lai không
        if (isDobInFuture(request.getDob())) {
            bindingResult.rejectValue("dob", "error.dob", "Ngày sinh không hợp lệ hoặc nằm trong tương lai");
            model.addAttribute("customer", request);
            return "AboutAccount/register"; // Trả về thông báo lỗi nếu ngày sinh không hợp lệ
        }

        if(customerService.doesCustomerExistByEmail(request.getEmail())){
            bindingResult.rejectValue("email", "error.email", "Email đã tồn tại");
            model.addAttribute("customer", request);
            return "AboutAccount/register"; // Trả về thông báo lỗi nếu email đã tồn tại
        }

        if(customerService.doesCustomerExistByCCCD(request.getCCCD())){
            bindingResult.rejectValue("CCCD", "error.CCCD", "CCCD đã tồn tại");
            model.addAttribute("customer", request);
            return "AboutAccount/register"; // Trả về thông báo lỗi nếu CCCD đã tồn tại
        }

        try {
            customerService.createNewCustomer(request);
            // Thêm thông báo thành công vào FlashAttributes
            redirectAttributes.addFlashAttribute("successMessage", "Tài khoản của bạn đã được tạo thành công!");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Đã có lỗi xảy ra khi tạo tài khoản. Vui lòng thử lại sau!");
            System.out.println("LỖI: " + e.getMessage());
            return "AboutAccount/register";
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
