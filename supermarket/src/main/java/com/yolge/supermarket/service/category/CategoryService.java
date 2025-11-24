package com.yolge.supermarket.service.category;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.category.CategoryRequest;
import com.yolge.supermarket.dto.category.CategoryResponse;
import com.yolge.supermarket.dto.category.DeleteCategoryResponse;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateById(Long id, CategoryRequest request);
    DeleteCategoryResponse deleteById(Long id);
    CategoryResponse getById(Long id);
    PageResponse<CategoryResponse> getAll(int page, int size);
    PageResponse<CategoryResponse> searchByName(int page, int size, String name);
}