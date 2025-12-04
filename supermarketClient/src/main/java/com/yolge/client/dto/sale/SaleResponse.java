package com.yolge.client.dto.sale;

import com.yolge.client.dto.client.ClientResponse;
import com.yolge.client.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleResponse {
    private Long id;
    private String status;
    private Double totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;

    private ClientResponse client;
    private UserResponse cashier;

    private List<SaleDetailResponse> details;
}