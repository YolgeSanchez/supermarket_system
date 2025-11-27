package com.yolge.supermarket.repository;

import com.yolge.supermarket.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Page<Client> findAllByDeletedAtIsNull(Pageable pageable);
    Optional<Client> findByIdAndDeletedAtIsNull(Long id);
    Optional<Client> findByDniAndDeletedAtIsNull(String dni);
    Page<Client> findByNameContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String name);
    Page<Client> findByEmailContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String email);
    boolean existsByDniAndDeletedAtIsNull(String dni);
    boolean existsByEmailAndDeletedAtIsNull(String email);
}
