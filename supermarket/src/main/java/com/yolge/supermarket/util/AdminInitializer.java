package com.yolge.supermarket.util;

import com.yolge.supermarket.dto.auth.RegisterRequest;
import com.yolge.supermarket.enums.Role;
import com.yolge.supermarket.entity.User;
import com.yolge.supermarket.repository.UserRepository;
import com.yolge.supermarket.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthService authService;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByRoleAndDeletedAtIsNull(Role.ADMIN)) {
            
            RegisterRequest admin = new RegisterRequest();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setFullName("adminadmin");
            admin.setRole("admin");

            authService.register(admin);
        } else {
            System.out.println("Ya existen administradores en la base de datos. Se omite la creación.");
        }
    }
}