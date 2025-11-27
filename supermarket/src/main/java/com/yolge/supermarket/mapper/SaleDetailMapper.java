package com.yolge.supermarket.mapper;

import com.yolge.supermarket.dto.saleDetail.SaleDetailResponse;
import com.yolge.supermarket.entity.SaleDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SaleDetailMapper {

    private final ProductMapper productMapper;

    public SaleDetailResponse toDto(SaleDetail detail) {
        SaleDetailResponse dto = new SaleDetailResponse();
        dto.setId(detail.getId());
        dto.setQuantity(detail.getQuantity());
        dto.setUnitPrice(detail.getUnitPrice());
        dto.setSubTotal(detail.getSubTotal());
        dto.setProduct(productMapper.toDto(detail.getProduct()));

        return dto;
    }

    public List<SaleDetailResponse> toDtoList(List<SaleDetail> details) {
        return details.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
