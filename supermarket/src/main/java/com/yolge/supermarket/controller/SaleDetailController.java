package com.yolge.supermarket.controller;

import com.yolge.supermarket.dto.sale.SaleResponse;
import com.yolge.supermarket.entity.SaleDetail;
import com.yolge.supermarket.service.saleDetail.SaleDetailService;
import com.yolge.supermarket.service.sale.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0.0/sales/details")
@RequiredArgsConstructor
public class SaleDetailController {

    private final SaleDetailService saleDetailService;
    private final SaleService saleService;

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    public ResponseEntity<SaleResponse> updateQuantity(
            @PathVariable Long id,
            @RequestParam Integer qty) {
        SaleDetail updatedDetail = saleDetailService.UpdateById(id, qty);

        Long saleId = updatedDetail.getSale().getId();
        return ResponseEntity.ok(saleService.getById(saleId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    public ResponseEntity<SaleResponse> removeDetail(@PathVariable Long id) {
        SaleDetail detail = saleDetailService.getById(id);
        Long saleId = detail.getSale().getId();

        saleDetailService.removeById(id);

        return ResponseEntity.ok(saleService.getById(saleId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    public ResponseEntity<SaleDetail> getById(@PathVariable Long id) {
        return ResponseEntity.ok(saleDetailService.getById(id));
    }
}