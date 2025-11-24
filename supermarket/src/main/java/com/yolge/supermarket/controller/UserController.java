package com.yolge.supermarket.controller;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.user.*;
import com.yolge.supermarket.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0.0/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username) {
        if (username != null && !username.isBlank())
            return ResponseEntity.ok(userService.searchByUsername(page, size, username));

        return ResponseEntity.ok(userService.getAll(page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@userSecurityService.isAccountOwner(authentication.principal.username, #id) OR hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeleteUserResponse> deleteById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@userSecurityService.isAccountOwner(authentication.principal.username, #id) OR hasRole('ADMIN')")
    public ResponseEntity<UpdateUserResponse> updateById(@RequestBody @Valid UpdateUserRequest request, @PathVariable Long id) {
        return ResponseEntity.ok(userService.update(id, request));
    }
}
