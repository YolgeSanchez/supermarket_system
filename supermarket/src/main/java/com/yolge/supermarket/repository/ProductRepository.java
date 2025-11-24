package com.yolge.supermarket.repository;

import com.yolge.supermarket.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByDeletedAtIsNull(Pageable pageable);
    Optional<Product> findByIdAndDeletedAtIsNull(Long id);
    Page<Product> findAllByNameContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name);
    Page<Product> findAllByBrandContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String brand);
    Page<Product> findAllByCategoryIdAndDeletedAtIsNull(Pageable pageable, Long categoryId);
    Page<Product> findAllByCategoryNameContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String categoryName);
    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);
}
