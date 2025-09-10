package com.example.SellPhone.Controller.Sell;

import com.example.SellPhone.DTO.Request.CheckOut.CheckOutRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/checkout")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckOutController {

    @PostMapping
    public ResponseEntity<?> handleCheckout(
            @RequestBody CheckOutRequest request,
            Principal principal,
            HttpServletRequest httpRequest,
            HttpSession session
    ) {
        if (principal == null) {
            String redirect = "/login?redirect=" + httpRequest.getRequestURI();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("redirectUrl", redirect));
        }

        // Có thể lưu vào session hoặc redirect luôn đến trang thanh toán với dữ liệu
        session.setAttribute("checkoutData", request);
        return ResponseEntity.ok(Map.of("redirectUrl", "/user/checkout/view"));
    }

    @GetMapping("/view")
    public String showCheckoutPage(HttpSession session, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login?redirect=/checkout/view";
        }

        CheckOutRequest data = (CheckOutRequest) session.getAttribute("checkoutData");
        if (data == null) {
            return "redirect:/"; // Hoặc show lỗi
        }

        model.addAttribute("checkoutData", data);
        return "Sell/checkout"; // Trang checkout.html
    }
}
