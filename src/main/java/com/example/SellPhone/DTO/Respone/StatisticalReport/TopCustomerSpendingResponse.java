package com.example.SellPhone.DTO.Respone.StatisticalReport;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopCustomerSpendingResponse {
     Long userId;
     String fullname;
     Long totalSpent;
     Long totalOrders;
}
