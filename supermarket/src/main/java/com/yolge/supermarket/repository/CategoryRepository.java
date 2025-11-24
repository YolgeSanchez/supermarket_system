package com.yolge.supermarket.repository;

import com.yolge.supermarket.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findAllByDeletedAtIsNull(Pageable pageable);
    Optional<Category> findByIdAndDeletedAtIsNull(Long id);
    Page<Category> findAllByNameContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name);
    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);
}
