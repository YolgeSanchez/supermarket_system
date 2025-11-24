package com.yolge.supermarket.service.auth;

import com.yolge.supermarket.config.JwtService;
import com.yolge.supermarket.dto.auth.AuthRequest;
import com.yolge.supermarket.dto.auth.AuthResponse;
import com.yolge.supermarket.dto.auth.RegisterRequest;
import com.yolge.supermarket.entity.User;
import com.yolge.supermarket.exceptions.ConflictException;
import com.yolge.supermarket.exceptions.NotFoundException;
import com.yolge.supermarket.mapper.AuthMapper;
import com.yolge.supermarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsernameAndDeletedAtIsNull(request.getUsername())) {
            throw new ConflictException("Este nombre de usuario se encuentra en uso");
        }

        User user = authMapper.toEntity(request);
        userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(user));
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = this.getByUsername(request.getUsername());
        return new AuthResponse(jwtService.generateToken(user));
    }

    private User getByUsername(String username) {
        return userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado!"));
    }
}
