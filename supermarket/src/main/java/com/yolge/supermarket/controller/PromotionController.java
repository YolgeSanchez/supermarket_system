package com.yolge.supermarket.controller;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.promotion.DeletePromotionResponse;
import com.yolge.supermarket.dto.promotion.PromotionRequest;
import com.yolge.supermarket.dto.promotion.PromotionResponse;
import com.yolge.supermarket.service.promotion.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0.0/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INVENTORY')")
    public ResponseEntity<PromotionResponse> create(@RequestBody @Valid PromotionRequest request) {
        PromotionResponse response = promotionService.createPromotion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeletePromotionResponse> deleteById(@PathVariable Long id) {
        DeletePromotionResponse response = promotionService.deleteById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INVENTORY', 'USER')")
    public ResponseEntity<PromotionResponse> getById(@PathVariable Long id) {
        PromotionResponse response = promotionService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INVENTORY', 'USER')")
    public ResponseEntity<PageResponse<PromotionResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name ) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(promotionService.searchByName(page, size, name));
        }

        return ResponseEntity.ok(promotionService.getAll(page, size));
    }
}