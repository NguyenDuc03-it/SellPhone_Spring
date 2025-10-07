package com.example.SellPhone.Controller.Sell;

import com.example.SellPhone.Config.CustomUserDetails;
import com.example.SellPhone.Config.VNPayConfig;
import com.example.SellPhone.DTO.Request.CheckOut.CheckOutRequest;
import com.example.SellPhone.DTO.Request.Order.OrderUpdateRequest;
import com.example.SellPhone.Entity.Order;
import com.example.SellPhone.Service.CustomerService;
import com.example.SellPhone.Service.OrderService;
import com.example.SellPhone.Service.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/checkout")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckOutController {

    CustomerService customerService;
    OrderService orderService;
    ShoppingCartService shoppingCartService;

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

        // chuyển thành danh sách để dùng chung trong /view
        List<CheckOutRequest> requestList = List.of(request);

        // Lưu vào session để redirect luôn đến trang thanh toán với dữ liệu
        session.setAttribute("checkoutData", requestList);
        return ResponseEntity.ok(Map.of("redirectUrl", "/user/checkout/view"));
    }

    @PostMapping("/from-cart")
    public ResponseEntity<?> handleCartCheckout(
            @RequestBody List<CheckOutRequest> requestList,
            Principal principal,
            HttpServletRequest httpRequest,
            HttpSession session
    ) {
        if (principal == null) {
            String redirect = "/login?redirect=" + httpRequest.getRequestURI();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("redirectUrl", redirect));
        }

        if (requestList == null || requestList.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Danh sách sản phẩm không hợp lệ"));
        }

        session.setAttribute("checkoutData", requestList);
        return ResponseEntity.ok(Map.of("redirectUrl", "/user/checkout/view"));
    }

    @GetMapping("/view")
    public String showCheckoutPage(HttpSession session, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login?redirect=/checkout/view";
        }
        Long userId = ((CustomUserDetails) ((Authentication) principal).getPrincipal()).getUserId();

        List<CheckOutRequest> data = (List<CheckOutRequest>) session.getAttribute("checkoutData");
        if (data == null) {
            return "redirect:/"; // Hoặc show lỗi
        }
        int total = data.stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();

        model.addAttribute("checkoutData", data);
        model.addAttribute("totalPrice", total);
        model.addAttribute("customer", customerService.getCustomerById(userId));
        return "Sell/checkout"; // Trang checkout.html
    }

    // Thanh toán
    @GetMapping("/create")
    public RedirectView createPayment(@RequestParam("amount") long amount,
                                      @RequestParam("orderId") Long orderId,
                                      @RequestParam(value = "bankCode", required = false) String bankCode,
                                      HttpServletRequest request) throws Exception {

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";

        long amountVnd = amount * 100; // VNPAY yêu cầu nhân 100
        String vnp_TxnRef = String.valueOf(orderId);
        String vnp_IpAddr = VNPayConfig.getIpAddress(request);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountVnd));
        vnp_Params.put("vnp_CurrCode", "VND");
        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toán đơn hàng: " + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Tạo query string và secure hash
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext();) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                        .append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + query.toString();

        // Redirect sang VNPAY
        return new RedirectView(paymentUrl);
    }

    @GetMapping("/vnpay-return")
    public String paymentReturn(HttpServletRequest request, Model model) {

        String queryString = request.getQueryString();
        Map<String, String> vnp_Params = new HashMap<>();
        String vnp_SecureHash = null;

        List<String> fieldNames = new ArrayList<>();

        if (queryString != null) {
            String[] params = queryString.split("&");
            for (String param : params) {
                String[] parts = param.split("=", 2);
                String key = parts[0];
                String value = parts.length > 1 ? parts[1] : "";

                if (key.equals("vnp_SecureHash")) {
                    vnp_SecureHash = value;
                } else {
                    vnp_Params.put(key, value);
                    fieldNames.add(key);
                }
            }
        }

        // Tạo lại dữ liệu để hash
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = vnp_Params.get(fieldName);
            sb.append(fieldName).append('=').append(fieldValue);
            if (i < fieldNames.size() - 1) {
                sb.append('&');
            }
        }

        // Hash với secretKey
        String signValue = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, sb.toString());

        boolean isValidSignature = signValue.equalsIgnoreCase(vnp_SecureHash);

        model.addAttribute("isValidSignature", isValidSignature);
        model.addAttribute("params", vnp_Params);

        if (isValidSignature) {
            String responseCode = vnp_Params.get("vnp_ResponseCode");
            if ("00".equals(responseCode)) {
                // Cập nhật trạng thái "Chờ xử lý" nếu thanh toán thành công
                Long orderId = Long.parseLong(vnp_Params.get("vnp_TxnRef"));

                Optional<Order> optionalOrder = orderService.findById(orderId);
                if (optionalOrder.isEmpty()) {
                    model.addAttribute("paymentStatus", "Không tìm thấy đơn hàng");
                    return "Sell/payment-result";
                }
                Order order = optionalOrder.get();

                OrderUpdateRequest orderUpdateRequest = new OrderUpdateRequest();
                orderUpdateRequest.setOrderId(orderId);
                orderUpdateRequest.setStatus("Chờ xử lý");
                orderService.updateOrderStatus(orderUpdateRequest);

                orderService.reduceStockAfterOrder(order);

                model.addAttribute("paymentStatus", "Thành công");
            } else {
                model.addAttribute("paymentStatus", "Thất bại - Mã lỗi: " + responseCode);
            }
        } else {
            model.addAttribute("paymentStatus", "Chữ ký không hợp lệ");
        }

        return "Sell/payment-result"; // thymeleaf template để hiển thị kết quả
    }

    @PostMapping("/payment")
    public ResponseEntity<?> handlePayment(
            @RequestParam("method") String method,
            @RequestParam("address") String address,
            @RequestParam(value = "bankCode", required = false) String bankCode,
            HttpSession session,
            Principal principal
    ) throws Exception {
        List<CheckOutRequest> checkoutList = (List<CheckOutRequest>) session.getAttribute("checkoutData");
        if (checkoutList == null || checkoutList.isEmpty()) {
            return ResponseEntity.badRequest().body("Không có dữ liệu giỏ hàng");
        }

        Long userId = ((CustomUserDetails) ((Authentication) principal).getPrincipal()).getUserId();

        orderService.validateStockBeforeOrder(checkoutList);

        // Xóa các item trong giỏ hàng khỏi giỏ sau khi để tạo đơn hàng
        List<Long> cartItemIdsToRemove = checkoutList.stream()
                .map(CheckOutRequest::getCartItemId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!cartItemIdsToRemove.isEmpty()) {
            shoppingCartService.deleteCartItemById(userId, cartItemIdsToRemove);
        }
        // Tạo đơn hàng
        Order order = orderService.createOrder(checkoutList, userId, address, method);

        if ("cod".equalsIgnoreCase(method)) {
            orderService.reduceStockAfterOrder(order);
            return ResponseEntity.ok(Map.of("redirectUrl", "/user/profile?section=orders"));
        } else {
            // VNPay: Redirect đến URL thanh toán với orderId
            String redirectUrl = "/user/checkout/create?amount=" + order.getTotalPrice()
                    + "&orderId=" + order.getOrderId()
                    + (bankCode != null ? "&bankCode=" + bankCode : "");
            return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
        }
    }

    @GetMapping("/result")
    public String showResultPage() {
        return "Sell/payment-result";
    }

}
