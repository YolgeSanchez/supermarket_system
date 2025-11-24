package com.yolge.supermarket.service.user;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.user.DeleteUserResponse;
import com.yolge.supermarket.dto.user.UpdateUserRequest;
import com.yolge.supermarket.dto.user.UpdateUserResponse;
import com.yolge.supermarket.dto.user.UserResponse;
import com.yolge.supermarket.entity.User;
import com.yolge.supermarket.exceptions.NotFoundException;
import com.yolge.supermarket.mapper.PageMapper;
import com.yolge.supermarket.mapper.UserMapper;
import com.yolge.supermarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PageMapper pageMapper;

    @Override
    public PageResponse<UserResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
        Page<User> users = userRepository.findAllByDeletedAtIsNull(pageable);
        List<UserResponse> mappedUsers = userMapper.toDtoList(users.getContent());
        return pageMapper.toDto(mappedUsers, users);
    }

    @Override
    public UserResponse getById(Long id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado!"));
        return userMapper.toDto(user);
    }

    @Override
    public PageResponse<UserResponse> searchByUsername(int page, int size, String username) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
        Page<User> user = userRepository.findAllByUsernameContainingIgnoreCaseAndDeletedAtIsNull(pageable, username);
        List<UserResponse> mappedUsers = userMapper.toDtoList(user.getContent());
        return pageMapper.toDto(mappedUsers, user);
    }

    @Override
    @Transactional
    public DeleteUserResponse deleteById(Long id) {
        User user = this.getByIdEntity(id);
        user.softDelete();
        return new DeleteUserResponse("Usuario eliminado correctamente!", id);
    }

    @Override
    @Transactional
    public UpdateUserResponse update(Long id, UpdateUserRequest request) {
        User user = this.getByIdEntity(id);
        userMapper.updateEntity(user, request);
        return new UpdateUserResponse("Usuario actualizado correctamente!", id);
    }

    private User getByIdEntity(Long id) {
        return userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado!"));
    }
}
