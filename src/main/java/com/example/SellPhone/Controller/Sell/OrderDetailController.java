package com.example.SellPhone.Controller.Sell;

import com.example.SellPhone.DTO.Respone.Order.OrderResponse;
import com.example.SellPhone.DTO.Respone.Order.ProductInfoInOrderResponse;
import com.example.SellPhone.Entity.Order;
import com.example.SellPhone.Service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/profile/order-history/order-detail")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderDetailController {

    OrderService orderService;

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Optional<Order> orderOptionalt = orderService.findById(id); // Gồm cả user + orderItems + product
        if(orderOptionalt.isPresent())
        {
            Order order = orderOptionalt.get();
            OrderResponse orderDTO = new OrderResponse();

            orderDTO.setOrderId(order.getOrderId());
            orderDTO.setFullname(order.getUser().getFullname());
            orderDTO.setPhone(order.getUser().getPhone());
            orderDTO.setAddress(order.getUser().getAddress());
            orderDTO.setOrderTime(order.getOrderTime());
            orderDTO.setDeliveryTimeEnd(order.getDeliveryTimeEnd());
            orderDTO.setOrderStatus(order.getOrderStatus());
            orderDTO.setPaymentMethod(order.getPaymentMethod());
            orderDTO.setTotalPrice(order.getTotalPrice());

            List<ProductInfoInOrderResponse> productInfoDTOs = order.getOrderItems().stream()
                    .map(productInfo -> {
                        if (productInfo.getProduct() == null) {
                            return ProductInfoInOrderResponse.builder()
                                    .productId(null)
                                    .name("[Sản phẩm không còn tồn tại]")
                                    .rom(productInfo.getRom())
                                    .color("")
                                    .price(productInfo.getPrice())
                                    .quantity(productInfo.getQuantity())
                                    .imageUrl("/images/no-product.png") // ảnh placeholder nếu muốn
                                    .build();
                        }
                        return ProductInfoInOrderResponse.builder()
                                .productId(productInfo.getProduct().getProductId())
                                .name(productInfo.getProduct().getName())
                                .rom(productInfo.getRom())
                                .color(productInfo.getProduct().getColor())
                                .price(productInfo.getPrice())
                                .quantity(productInfo.getQuantity())
                                .imageUrl(productInfo.getProduct().getImageUrl())
                                .build();
                    }).toList();
            orderDTO.setProductInfos(productInfoDTOs);
            model.addAttribute("order", orderDTO);
            return "Sell/order-detail";
        }
        else{
            model.addAttribute("errorMessage", "Không tìm thấy sản phẩm");

            return "Sell/order-detail";
        }
    }
}
