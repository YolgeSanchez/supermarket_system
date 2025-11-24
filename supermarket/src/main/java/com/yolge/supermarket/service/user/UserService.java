package com.yolge.supermarket.service.user;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.user.DeleteUserResponse;
import com.yolge.supermarket.dto.user.UpdateUserRequest;
import com.yolge.supermarket.dto.user.UpdateUserResponse;
import com.yolge.supermarket.dto.user.UserResponse;
import com.yolge.supermarket.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    PageResponse<UserResponse> getAll(int page, int size);
    UserResponse getById(Long id);
    PageResponse<UserResponse> searchByUsername(int page, int size, String username);
    DeleteUserResponse deleteById(Long id);
    UpdateUserResponse update(Long id, UpdateUserRequest request);
}
