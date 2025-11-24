package com.yolge.supermarket.service.product;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.product.DeleteProductResponse;
import com.yolge.supermarket.dto.product.ProductRequest;
import com.yolge.supermarket.dto.product.ProductResponse;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateById(Long id, ProductRequest request);
    DeleteProductResponse deleteById(Long id);
    PageResponse<ProductResponse> getAll(int page, int size);
    ProductResponse getById(Long id);
    PageResponse<ProductResponse> searchByCategoryId(int page, int size, Long categoryId);
    PageResponse<ProductResponse> searchByName(int page, int size, String name);
    PageResponse<ProductResponse> searchByBrand(int page, int size, String brand);
    PageResponse<ProductResponse> searchByCategoryName(int page, int size, String categoryName);
    void decreaseStockById(Long id, int quantity);
    ProductResponse increaseStockById(Long id, int quantity);
}