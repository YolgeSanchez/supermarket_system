package com.yolge.supermarket.service.category;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.category.CategoryRequest;
import com.yolge.supermarket.dto.category.CategoryResponse;
import com.yolge.supermarket.dto.category.DeleteCategoryResponse;
import com.yolge.supermarket.entity.Category;
import com.yolge.supermarket.exceptions.ConflictException;
import com.yolge.supermarket.exceptions.NotFoundException;
import com.yolge.supermarket.mapper.CategoryMapper;
import com.yolge.supermarket.mapper.PageMapper;
import com.yolge.supermarket.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final PageMapper pageMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(request.getName())) {
            throw new ConflictException("Ya existe una categoria con ese nombre!");
        }

        Category category = categoryMapper.toEntity(request);
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponse updateById(Long id, CategoryRequest request) {
        Category category = this.getByIdEntity(id);

        categoryMapper.updateEntity(category, request);
        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    @Transactional
    public DeleteCategoryResponse deleteById(Long id) {
        Category category = this.getByIdEntity(id);
        category.softDelete();

        return new DeleteCategoryResponse("La categoria ha sido eliminada exitosamente!", id);
    }

    @Override
    public CategoryResponse getById(Long id) {
        Category category = this.getByIdEntity(id);
        return categoryMapper.toDto(category);
    }

    @Override
    public PageResponse<CategoryResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Category> category = categoryRepository.findAllByDeletedAtIsNull(pageable);
        List<CategoryResponse> categoryResponses = categoryMapper.toDtoList(category.getContent());

        return pageMapper.toDto(categoryResponses, category);
    }

    @Override
    public PageResponse<CategoryResponse> searchByName(int page, int size, String name) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Category> category = categoryRepository.findAllByNameContainingIgnoreCaseAndDeletedAtIsNull(pageable, name);
        List<CategoryResponse> categoryResponses = categoryMapper.toDtoList(category.getContent());

        return pageMapper.toDto(categoryResponses, category);
    }

    private Category getByIdEntity(Long id) {
        return categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Categoria no encontrada!"));
    }
}
