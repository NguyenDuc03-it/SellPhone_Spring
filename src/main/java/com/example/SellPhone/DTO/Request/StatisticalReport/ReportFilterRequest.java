package com.example.SellPhone.DTO.Request.StatisticalReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportFilterRequest {
    private String reportType; // revenue, bestsellers, customers
    private String startDate;
    private String endDate;
}