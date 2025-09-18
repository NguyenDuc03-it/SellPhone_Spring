package com.example.SellPhone.Controller.Sell;

import com.example.SellPhone.Config.CustomUserDetails;
import com.example.SellPhone.DTO.Request.User.UserProfileUpdateRequest;
import com.example.SellPhone.DTO.Respone.Order.OrderResponse;
import com.example.SellPhone.DTO.Respone.Order.ProductInfoInOrderResponse;
import com.example.SellPhone.Entity.Order;
import com.example.SellPhone.Entity.User;
import com.example.SellPhone.Service.CustomerService;
import com.example.SellPhone.Service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/profile")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {

    CustomerService customerService;
    PasswordEncoder passwordEncoder;
    OrderService orderService;

    @GetMapping
    public String viewProfile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login?redirect=/user/profile"; // Chuyển hướng đến trang đăng nhập nếu chưa đăng nhập
        }
        Long userId = ((CustomUserDetails) ((Authentication) principal).getPrincipal()).getUserId();

        User user = customerService.getCustomerById(userId);
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
        return "Sell/profile";
    }

    // Cập nhật thông tin cá nhân
    @PostMapping("/update-info")
    public String updateProfile(@ModelAttribute("user") @Valid UserProfileUpdateRequest request,
                                BindingResult result,
                                Principal principal,
                                Model model,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login?redirect=/user/profile";
        }

        Long userId = ((CustomUserDetails) ((Authentication) principal).getPrincipal()).getUserId();

        if (result.hasErrors()) {
            model.addAttribute("user", request);
            return "Sell/profile"; // Load lại trang với lỗi
        }

        // Đảm bảo userId trong form là chính chủ
        if (!request.getUserId().equals(userId)) {
            model.addAttribute("errorMessage", "Chỉ có thể sửa thông tin của chính bạn");
            return "Sell/profile"; // Load lại trang với lỗi
        }

        // Kiểm tra ngày sinh có phải trong tương lai không
        if (isDobInFuture(request.getDob())) {
            model.addAttribute("errorMessage", "Ngày sinh không thể là ngày trong tương lai");
            return "Sell/profile"; // Trả về thông báo lỗi nếu ngày sinh không hợp lệ
        }

        if(customerService.doesCustomerExistByCCCD(request.getCCCD()) && !request.getCCCD().equals(customerService.getCustomerById(request.getUserId()).getCCCD())){
            model.addAttribute("errorMessage", "CCCD đã tồn tại");
            return "Sell/profile"; // Trả về thông báo lỗi nếu CCCD đã tồn tại
        }

        try {
            customerService.updateCustomerInfo(request); // Gọi service để cập nhật

            User user = customerService.getCustomerById(userId);
            String dob = user.getDob(); // Lấy ngày sinh dạng dd/MM/yyyy

            // Tạo authorities dựa trên role (entity User của bạn chỉ lưu 1 role dạng String)
            Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRoleWithPrefix()));

            // Tạo CustomUserDetails mới từ user
            CustomUserDetails updatedUserDetails = new CustomUserDetails(
                    user.getUserId(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getFullname(),
                    authorities,
                    "Hoạt động".equals(user.getStatus()) // Kiểm tra trạng thái hoạt động
            );

            // Lấy authentication hiện tại
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

            // Tạo authentication mới với CustomUserDetails mới
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedUserDetails,
                    currentAuth.getCredentials(),
                    updatedUserDetails.getAuthorities()
            );

            // Cập nhật Authentication trong SecurityContext
            SecurityContextHolder.getContext().setAuthentication(newAuth);

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
            return "redirect:/user/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/profile"; // Chuyển hướng về trang profile
        }
    }

    // Đổi mật khẩu
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
        User user = customerService.getCustomerById(userId);

        // So sánh mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("currentPasswordSave", currentPassword);
            redirectAttributes.addFlashAttribute("newPasswordSave", newPassword);
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu hiện tại không đúng");
            return "redirect:/user/profile?section=password";
        }

        if(currentPassword.equals(newPassword)){
            redirectAttributes.addFlashAttribute("currentPasswordSave", currentPassword);
            redirectAttributes.addFlashAttribute("newPasswordSave", newPassword);
            redirectAttributes.addFlashAttribute("newPasswordMessage", "Mật khẩu mới không được trùng với mật khẩu hiện tại");
            return "redirect:/user/profile?section=password";
        }

        try {
            customerService.changeCustomerPassword(userId, newPassword);

            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công");
            return "redirect:/user/profile?section=password";
        }
        catch(Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/profile?section=password";
        }
    }

    // Xem lịch sử đơn hàng
    @GetMapping("/order-history")
    @ResponseBody
    public ResponseEntity<List<OrderResponse>> getOrderHistory(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        Long userId = ((CustomUserDetails) ((Authentication) principal).getPrincipal()).getUserId();

        List<Order> orders = orderService.findOrdersByUserId(userId); // cần thêm method này

        List<OrderResponse> orderResponses = orders.stream().map(order -> {
            List<ProductInfoInOrderResponse> productInfos = order.getOrderItems().stream().map(item ->
                    ProductInfoInOrderResponse.builder()
                            .name(item.getProduct().getName())
                            .rom(item.getRom())
                            .color(item.getProduct().getColor())
                            .price(item.getPrice())
                            .quantity(item.getQuantity())
                            .imageUrl(item.getProduct().getImageUrl())
                            .productId(item.getProduct().getProductId())
                            .build()
            ).toList();

            OrderResponse response = new OrderResponse();
            response.setOrderId(order.getOrderId());
            response.setFullname(order.getUser().getFullname());
            response.setPhone(order.getUser().getPhone());
            response.setAddress(order.getUser().getAddress());
            response.setOrderTime(order.getOrderTime());
            response.setOrderStatus(order.getOrderStatus());
            response.setPaymentMethod(order.getPaymentMethod());
            response.setTotalPrice(order.getTotalPrice());
            response.setProductInfos(productInfos);

            return response;
        }).toList();

        return ResponseEntity.ok(orderResponses);
    }

    // Hủy đơn hàng
    @PostMapping("/cancel-order")
    String cancelOrder(@RequestParam("orderId") Long orderId, RedirectAttributes redirectAttributes, Model model){
        if(orderId == null){
            redirectAttributes.addFlashAttribute("errorMessage", "Không nhận được mã đơn hàng");
            return "redirect:/user/profile?section=orders"; // Chuyển hướng về trang danh sách đơn hàng
        }
        Optional<Order> orderOptionalt = orderService.findById(orderId);
        if(orderOptionalt.isPresent()) {
            Order order = orderOptionalt.get();
            if(order.getOrderStatus().equals("Đã hoàn thành")){
                redirectAttributes.addFlashAttribute("errorMessage", "Đơn hàng đã hoàn thành, không thể hủy");
                return "redirect:/management/orders";
            }

            try {
                orderService.cancelOrder(order);
                redirectAttributes.addFlashAttribute("successMessage", "Hủy đơn hàng thành công!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            }

        }
        else{
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn hàng");
        }
        return "redirect:/user/profile?section=orders"; // Chuyển hướng về trang danh sách đơn hàng
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
