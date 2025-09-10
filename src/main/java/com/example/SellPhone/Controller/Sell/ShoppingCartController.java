package com.example.SellPhone.Controller.Sell;

import com.example.SellPhone.Config.CustomUserDetails;
import com.example.SellPhone.DTO.Request.ShoppingCart.CartRequest;
import com.example.SellPhone.Service.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShoppingCartController {

    ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody CartRequest request, Principal principal, HttpServletRequest httpRequest) {
        if (principal == null) {
            String currentUrl = httpRequest.getHeader("referer");
            Map<String, Object> response = new HashMap<>();
            response.put("redirect", "/login?redirect=" + currentUrl); // Trả về link login kèm redirect
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Long userId = ((CustomUserDetails) ((Authentication) principal).getPrincipal()).getUserId();

        shoppingCartService.addItemToCart(userId, request.getProductId(), request.getQuantity());

        int totalItems = shoppingCartService.countTotalCartItems(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalItems", totalItems);

        return ResponseEntity.ok(response);
    }

}
