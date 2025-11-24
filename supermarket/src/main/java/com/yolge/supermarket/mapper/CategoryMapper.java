package com.yolge.supermarket.mapper;

import com.yolge.supermarket.dto.category.CategoryRequest;
import com.yolge.supermarket.dto.category.CategoryResponse;
import com.yolge.supermarket.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryMapper {
    private final ProductMapper productMapper;

    public CategoryResponse toDto(Category category) {
        CategoryResponse dto = new CategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setProducts(productMapper.toDtoList(category.getProducts()));
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }

    public List<CategoryResponse> toDtoList(List<Category> categoryList) {
        return categoryList.stream()
                .map(this::toDto)
                .toList();
    }

    public Category toEntity(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return category;
    }

    public Category updateEntity(Category category, CategoryRequest request) {
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return category;
    }
}
