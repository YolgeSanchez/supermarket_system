package com.yolge.supermarket.repository;

import com.yolge.supermarket.entity.User;
import com.yolge.supermarket.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAllByDeletedAtIsNull(Pageable pageable);
    Optional<User> findByIdAndDeletedAtIsNull(Long id);
    boolean existsByUsernameAndDeletedAtIsNull(String username);
    Page<User> findAllByUsernameContainingIgnoreCaseAndDeletedAtIsNull(Pageable pageable, String username);
    boolean existsByUsernameAndIdAndDeletedAtIsNull(String username, Long id);
    Optional<User> findByUsernameAndDeletedAtIsNull(String username);
    boolean existsByRoleAndDeletedAtIsNull(Role role);
}
