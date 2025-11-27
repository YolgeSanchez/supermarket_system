package com.yolge.supermarket.mapper;

import com.yolge.supermarket.dto.sale.SaleResponse;
import com.yolge.supermarket.dto.user.UserResponse;
import com.yolge.supermarket.entity.Sale;
import com.yolge.supermarket.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SaleMapper {

    private final ClientMapper clientMapper;
    private final SaleDetailMapper saleDetailMapper;
    private final UserMapper userMapper;

    public SaleResponse toDto(Sale sale) {
        if (sale == null) return null;
        SaleResponse dto = new SaleResponse();
        dto.setId(sale.getId());
        dto.setTotalPrice(sale.getTotalPrice());
        dto.setCreatedAt(sale.getCreatedAt());
        dto.setFinishedAt(sale.getFinishedAt());
        dto.setStatus(String.valueOf(sale.getStatus()));

        dto.setClient(clientMapper.toDto(sale.getClient()));
        dto.setDetails(saleDetailMapper.toDtoList(sale.getSaleDetails()));
        dto.setCashier(userMapper.toDto(sale.getCashier()));

        return dto;
    }

    public List<SaleResponse> toDtoList(List<Sale> sales) {
        return sales.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
