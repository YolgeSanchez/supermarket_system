package com.yolge.supermarket.controller;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.category.CategoryRequest;
import com.yolge.supermarket.dto.category.CategoryResponse;
import com.yolge.supermarket.dto.category.DeleteCategoryResponse;
import com.yolge.supermarket.dto.product.ProductResponse;
import com.yolge.supermarket.service.category.CategoryService;
import com.yolge.supermarket.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0.0/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INVENTORY')")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INVENTORY')")
    public ResponseEntity<CategoryResponse> updateById(
            @PathVariable Long id,
            @RequestBody @Valid CategoryRequest request
    ) {
        CategoryResponse response = categoryService.updateById(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeleteCategoryResponse> deleteById(@PathVariable Long id) {
        DeleteCategoryResponse response = categoryService.deleteById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'INVENTORY')")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
        CategoryResponse response = categoryService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'INVENTORY')")
    public ResponseEntity<PageResponse<CategoryResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(categoryService.searchByName(page, size, name));
        }

        return ResponseEntity.ok(categoryService.getAll(page, size));
    }

    @GetMapping("/{id}/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'INVENTORY', 'USER')")
    public ResponseEntity<PageResponse<ProductResponse>> searchProductsByCategoryId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        categoryService.getById(id);
        PageResponse<ProductResponse> response = productService.searchByCategoryId(page, size, id);

        return ResponseEntity.ok(response);
    }
}
