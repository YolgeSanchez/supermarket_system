package com.yolge.supermarket.mapper;

import com.yolge.supermarket.dto.user.UpdateUserRequest;
import com.yolge.supermarket.dto.user.UserResponse;
import com.yolge.supermarket.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public UserResponse toDto(User user) {
        UserResponse dto = new UserResponse();
        dto.setUserId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setUsername(user.getUsername());
        dto.setRole(String.valueOf(user.getRole()));
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
		
    public List<UserResponse> toDtoList(List<User> users) {
        return users.stream()
                .map(this::toDto)
                .toList();
    }
		
    public void updateEntity(User user, UpdateUserRequest request) {
        user.setFullName(request.getFullName());
    }
}
