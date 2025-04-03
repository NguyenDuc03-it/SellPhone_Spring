package com.example.SellPhone.Controller.Auth;

import com.example.SellPhone.DTO.Request.Auth.AuthenticationRequest;
import com.example.SellPhone.Service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

//    @PostMapping("/login")
//    String login(@ModelAttribute("authentication") AuthenticationRequest request, Model model){
//        boolean authenticated = authenticationService.authenticate(request);
//
//        if(authenticated) return "redirect:/Sell/index";
//        else {
//            model.addAttribute("error", "Email hoặc mật khẩu không chính xác.");
//            return "redirect:/login?error";
//        }
//    }
}
