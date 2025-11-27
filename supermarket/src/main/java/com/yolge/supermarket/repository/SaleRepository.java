package com.yolge.supermarket.repository;

import com.yolge.supermarket.entity.Sale;
import com.yolge.supermarket.enums.SaleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    Page<Sale> findAll(Pageable pageable);
    Page<Sale> findAllByClientId(Pageable pageable, Long id);
    Page<Sale> findAllByCashierId(Pageable pageable, Long id);
    Page<Sale> findAllByStatus(Pageable pageable, SaleStatus status);
}
