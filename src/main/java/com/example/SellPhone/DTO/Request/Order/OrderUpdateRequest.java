package com.example.SellPhone.DTO.Request.Order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter

public class OrderUpdateRequest {
    @NotNull(message = "Không tìm thấy ID đơn hàng")
    @Min(value = 1, message = "ID đơn hàng không hợp lệ")
    private Long orderId;

    @NotBlank(message = "Trạng thái không được để trống")
    String status;
}
