package com.yolge.supermarket.controller;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.product.DeleteProductResponse;
import com.yolge.supermarket.dto.product.ProductRequest;
import com.yolge.supermarket.dto.product.ProductResponse;
import com.yolge.supermarket.dto.product.ProductStockRequest;
import com.yolge.supermarket.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0.0/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INVENTORY')")
    public ResponseEntity<ProductResponse> create(@RequestBody @Valid ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INVENTORY')")
    public ResponseEntity<ProductResponse> updateById(
            @PathVariable Long id,
            @RequestBody @Valid ProductRequest request
    ) {
        ProductResponse response = productService.updateById(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeleteProductResponse> deleteById(@PathVariable Long id) {
        DeleteProductResponse response = productService.deleteById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INVENTORY', 'USER')")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        ProductResponse response = productService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INVENTORY', 'USER')")
    public ResponseEntity<PageResponse<ProductResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String categoryName
    ) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(productService.searchByName(page, size, name));
        }

        if (brand != null && !brand.isBlank()) {
            return ResponseEntity.ok(productService.searchByBrand(page, size, brand));
        }

        if (categoryName != null && !categoryName.isBlank()) {
            return ResponseEntity.ok(productService.searchByCategoryName(page, size, categoryName));
        }

        // Si no hay filtros, devuelve todos
        return ResponseEntity.ok(productService.getAll(page, size));
    }

    @PatchMapping("/{id}/restock")
    @PreAuthorize("hasAnyRole('ADMIN', 'INVENTORY')")
    public ResponseEntity<ProductResponse> increaseStockById(
            @PathVariable Long id,
            @RequestBody @Valid ProductStockRequest request
    ) {
        ProductResponse response = productService.increaseStockById(id, request.getQuantity());
        return ResponseEntity.ok(response);
    }
}