package com.yolge.supermarket.mapper;

import com.yolge.supermarket.dto.category.CategorySlimResponse;
import com.yolge.supermarket.dto.product.ProductRequest;
import com.yolge.supermarket.dto.product.ProductResponse;
import com.yolge.supermarket.entity.Category;
import com.yolge.supermarket.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class ProductMapper {
    public ProductResponse toDto(Product product) {
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setBrand(product.getBrand());
        dto.setBasePrice(product.getBasePrice());
        dto.setTaxPercentage(product.getTaxPercentage());
        dto.setFinalPrice(BigDecimal.valueOf(product.getProductTotalPrice()).setScale(2, RoundingMode.HALF_UP));
        dto.setStock(product.getStock());
        dto.setCategory(this.categoryToSlimDto(product.getCategory()));
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

    public ProductResponse toDto(Product product, Double discount) {
        Double precioCalculado = product.getProductTotalPrice(discount);
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setBrand(product.getBrand());
        dto.setBasePrice(product.getBasePrice());
        dto.setTaxPercentage(product.getTaxPercentage());
        dto.setFinalPrice(BigDecimal.valueOf(precioCalculado).setScale(2, RoundingMode.HALF_UP));
        dto.setStock(product.getStock());
        dto.setCategory(this.categoryToSlimDto(product.getCategory()));
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

    public List<ProductResponse> toDtoList(List<Product> products) {
        return products.stream()
                .map(this::toDto)
                .toList();
    }

    public Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setBasePrice(request.getBasePrice());
        product.setTaxPercentage(request.getTaxPercentage());
        product.setStock(request.getStock());
        return product;
    }

    public void updateEntity(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setBasePrice(request.getBasePrice());
        product.setTaxPercentage(request.getTaxPercentage());
        product.setStock(request.getStock());
    }

    private CategorySlimResponse categoryToSlimDto(Category category) {
        CategorySlimResponse dto = new CategorySlimResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}

