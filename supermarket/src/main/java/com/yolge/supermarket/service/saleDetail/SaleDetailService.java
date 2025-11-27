package com.yolge.supermarket.service.saleDetail;

import com.yolge.supermarket.dto.saleDetail.SaleDetailRequest;
import com.yolge.supermarket.entity.SaleDetail;
import org.springframework.stereotype.Service;

@Service
public interface SaleDetailService {
    SaleDetail CreateDetail(Long saleId, SaleDetailRequest request);
    SaleDetail UpdateById(Long id, Integer qty);
    void removeById(Long id);
    SaleDetail getById(Long id);
}
