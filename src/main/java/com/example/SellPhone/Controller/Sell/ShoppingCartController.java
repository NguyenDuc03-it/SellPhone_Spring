package com.example.SellPhone.Controller.Sell;

import com.example.SellPhone.Config.CustomUserDetails;
import com.example.SellPhone.DTO.Request.ShoppingCart.CartRequest;
import com.example.SellPhone.DTO.Respone.ShoppingCart.CartItemRespone;
import com.example.SellPhone.Service.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/cart")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShoppingCartController {

    ShoppingCartService shoppingCartService;

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody CartRequest request, Principal principal, HttpServletRequest httpRequest) {
        if (principal == null) {
            String currentUrl = httpRequest.getHeader("referer");
            Map<String, Object> response = new HashMap<>();
            response.put("redirect", "/login?redirect=" + currentUrl); // Trả về link login kèm redirect
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Long userId = ((CustomUserDetails) ((Authentication) principal).getPrincipal()).getUserId();

        shoppingCartService.addItemToCart(userId, request.getProductId(), request.getQuantity(), request.getRom());

        int totalItems = shoppingCartService.countTotalCartItems(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalItems", totalItems);

        return ResponseEntity.ok(response);
    }

    // Mở trang giỏ hàng
    @GetMapping
    public String viewCart(Model mmodel, Principal principal) {
        if (principal == null) {
            return "redirect:/login?redirect=/user/cart"; // Chuyển hướng đến trang đăng nhập nếu chưa đăng nhập
        }
        Long userId = ((CustomUserDetails) ((Authentication) principal).getPrincipal()).getUserId();

        List<CartItemRespone> cartItems = shoppingCartService.getCartItemsByUserId(userId);
        mmodel.addAttribute("cartItems", cartItems);
        return "Sell/cart"; // Trả về view của trang giỏ hàng
    }

    @PostMapping("/update-quantity")
    public ResponseEntity<Map<String, Object>> updateCartItemQuantity(@RequestParam("cartItemId") Long id, @RequestParam("quantity") int quantity, Principal principal, HttpServletRequest httpRequest) {
        if (principal == null) {
            String currentUrl = httpRequest.getHeader("referer");
            Map<String, Object> response = new HashMap<>();
            response.put("redirect", "/login?redirect=" + currentUrl); // Trả về link login kèm redirect
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Long userId = ((CustomUserDetails) ((Authentication) principal).getPrincipal()).getUserId();

        Map<String, Object> response = new HashMap<>();
        if(id == null) {
            response.put("success", false);
            response.put("message", "Không nhận được thông tin sản phẩm");
            return ResponseEntity.badRequest().body(response);
        }

        if (quantity <= 0) {
            response.put("success", false);
            response.put("message", "Số lượng phải lớn hơn 0");
            return ResponseEntity.badRequest().body(response);
        }

        try{
            shoppingCartService.updateCartItemQuantity(userId, id, quantity);
            response.put("success", true);
            return ResponseEntity.ok(response);
        }
        catch(Exception e){
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    // Xoá một sản phẩm khỏi giỏ hàng
    @PostMapping("/delete-cart-item")
    public ResponseEntity<Map<String, Object>> deleteCartItem(@RequestParam("cartItemId") Long cartItemId, Principal principal, HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();

        if (principal == null) {
            String currentUrl = httpRequest.getHeader("referer");
            response.put("redirect", "/login?redirect=" + currentUrl);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Long userId = ((CustomUserDetails) ((Authentication) principal).getPrincipal()).getUserId();

        try {
            shoppingCartService.deleteCartItemById(userId, cartItemId);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Xoá nhiều sản phẩm khỏi giỏ hàng
    @PostMapping("/delete-multiple")
    public ResponseEntity<Map<String, Object>> deleteMultipleCartItems(@RequestParam("ids") List<Long> cartItemIds, Principal principal, HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();

        if (principal == null) {
            String currentUrl = httpRequest.getHeader("referer");
            response.put("redirect", "/login?redirect=" + currentUrl);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Long userId = ((CustomUserDetails) ((Authentication) principal).getPrincipal()).getUserId();

        try {
            for (Long cartItemId : cartItemIds) {
                shoppingCartService.deleteCartItemById(userId, cartItemId);
            }
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
