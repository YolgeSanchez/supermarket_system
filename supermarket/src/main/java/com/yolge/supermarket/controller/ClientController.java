package com.yolge.supermarket.controller;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.client.ClientRequest;
import com.yolge.supermarket.dto.client.ClientResponse;
import com.yolge.supermarket.dto.client.DeleteClientResponse;
import com.yolge.supermarket.service.client.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v0.0/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    public ResponseEntity<ClientResponse> create(@RequestBody @Valid ClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createClient(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    public ResponseEntity<ClientResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid ClientRequest request
    ) {
        return ResponseEntity.ok(clientService.updateById(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeleteClientResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.deleteById(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    public ResponseEntity<ClientResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getById(id));
    }

    @GetMapping("/dni/{dni}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    public ResponseEntity<ClientResponse> getByDni(@PathVariable String dni) {
        return ResponseEntity.ok(clientService.getByDni(dni));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    public ResponseEntity<PageResponse<ClientResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email
    ) {
        if (email != null && !email.isBlank()) {
            return ResponseEntity.ok(clientService.searchByEmail(page, size, email));
        }
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(clientService.searchByName(page, size, name));
        }
        return ResponseEntity.ok(clientService.getAll(page, size));
    }
}