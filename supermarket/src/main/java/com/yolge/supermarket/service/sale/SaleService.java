package com.yolge.supermarket.service.sale;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.sale.SaleResponse;
import org.springframework.stereotype.Service;

@Service
public interface SaleService {
    SaleResponse createSale(Long clientId, Long cashierId);
    SaleResponse getById(Long id);
    SaleResponse finishSale(Long id);
    SaleResponse cancelSale(Long id);
    PageResponse<SaleResponse> getAll(int page, int size);
    PageResponse<SaleResponse> getByClient(int page, int size, Long id);
    PageResponse<SaleResponse> getByCashier(int page, int size, Long id);
    PageResponse<SaleResponse> getBySaleStatus(int page, int size, String status);
}
