package com.yolge.supermarket.service.auth;

import com.yolge.supermarket.dto.auth.AuthRequest;
import com.yolge.supermarket.dto.auth.AuthResponse;
import com.yolge.supermarket.dto.auth.RegisterRequest;
import com.yolge.supermarket.entity.User;

public interface AuthService {
    AuthResponse register(RegisterRequest user);
    AuthResponse login(AuthRequest request);
}
