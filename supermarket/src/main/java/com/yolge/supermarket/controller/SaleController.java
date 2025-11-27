package com.yolge.supermarket.controller;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.sale.SaleRequest; // (DTO simple con clientId opcional)
import com.yolge.supermarket.dto.saleDetail.SaleDetailRequest;
import com.yolge.supermarket.dto.sale.SaleResponse;
import com.yolge.supermarket.entity.SaleDetail;
import com.yolge.supermarket.entity.User;
import com.yolge.supermarket.service.saleDetail.SaleDetailService;
import com.yolge.supermarket.service.sale.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0.0/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;
    private final SaleDetailService saleDetailService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    public ResponseEntity<SaleResponse> createSale(
            @RequestBody(required = false) SaleRequest request,
            @AuthenticationPrincipal User user) {
        Long clientId = (request != null) ? request.getClientId() : null;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saleService.createSale(clientId, user.getId()));
    }

    @PatchMapping("/{id}/finalize")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    public ResponseEntity<SaleResponse> finishSale(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.finishSale(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    public ResponseEntity<SaleResponse> cancelSale(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.cancelSale(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    public ResponseEntity<SaleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getById(id));
    }

    @PostMapping("/{saleId}/details")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    public ResponseEntity<SaleResponse> addDetail(
            @PathVariable Long saleId,
            @RequestBody @Valid SaleDetailRequest request) {
        saleDetailService.CreateDetail(saleId, request);
        return ResponseEntity.ok(saleService.getById(saleId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    public ResponseEntity<PageResponse<SaleResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long cashierId) {
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(saleService.getBySaleStatus(page, size, status));
        }
        if (clientId != null) {
            return ResponseEntity.ok(saleService.getByClient(page, size, clientId));
        }
        if (cashierId != null) {
            return ResponseEntity.ok(saleService.getByCashier(page, size, cashierId));
        }
        return ResponseEntity.ok(saleService.getAll(page, size));
    }
}