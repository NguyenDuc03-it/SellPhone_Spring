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
import java.util.Set;

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

        if (isInvalidPhoneNumber(request.getPhone())) {
            bindingResult.rejectValue("phone", "error.phone", "Số điện thoại không hợp lệ hoặc không tồn tại tại Việt Nam");
            model.addAttribute("customer", request);
            return "AboutAccount/register";
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

    private boolean isInvalidPhoneNumber(String phone) {
        // Normalize: +84xxx => 0xxx
        String normalized = phone.startsWith("+84")
                ? phone.replaceFirst("\\+84", "0")
                : phone;

        // Đúng độ dài 10 số
        if (normalized.length() != 10) {
            return true;
        }

        // Tách đầu số và phần còn lại
        String prefix = normalized.substring(0, 3);
        String numberPart = normalized.substring(3);

        // Kiểm tra đầu số hợp lệ
        Set<String> validPrefixes = Set.of(
                "032", "033", "034", "035", "036", "037", "038", "039",  // Viettel
                "070", "076", "077", "078", "079",                      // MobiFone
                "081", "082", "083", "084", "085",                      // Vinaphone
                "056", "058",                                           // Vietnamobile
                "059",                                                  // Gmobile
                "090", "093", "089",                                    // MobiFone cũ
                "091", "094", "088"                                     // Vinaphone cũ
        );

        if (!validPrefixes.contains(prefix)) {
            return true;
        }

        // Nếu phần còn lại là 7 số giống nhau (ví dụ 0000000, 9999999, ...)
        return numberPart.chars().distinct().count() == 1;// Hợp lệ
    }
}
