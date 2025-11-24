package com.yolge.supermarket.repository;

import com.yolge.supermarket.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    @Query("""
        SELECT COALESCE(MAX(p.discountPercentage), 0.0)
        FROM Promotion p
        JOIN p.products prod
        WHERE prod.id = :productId
          AND p.deletedAt IS NULL
          AND :now >= p.startDate 
          AND :now <= p.endDate
    """)
    Double findMaxDiscountForProduct(@Param("productId") Long id, @Param("now") LocalDateTime now);
    Page<Promotion> findAllByDeletedAtIsNull(Pageable pageable);
    Page<Promotion> findByNameContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name);
    Optional<Promotion> findByIdAndDeletedAtIsNull(Long id);
}
