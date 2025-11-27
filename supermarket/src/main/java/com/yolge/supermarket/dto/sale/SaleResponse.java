package com.yolge.supermarket.dto.sale;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.yolge.supermarket.dto.client.ClientResponse;
import com.yolge.supermarket.dto.saleDetail.SaleDetailResponse;
import com.yolge.supermarket.dto.user.UserResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonPropertyOrder({
    "id",
    "status",
    "totalPrice",
    "createdAt",
    "finishedAt",
    "client",
    "cashier",
    "details"
})
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