package com.example.SellPhone.DTO.Respone.Order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long orderId;
    String fullname;
    String phone;
    String address;
    String orderTime;
    String orderStatus;
    String paymentMethod;
    Long totalPrice;
    List<ProductInfoInOrderResponse> productInfos;

}
