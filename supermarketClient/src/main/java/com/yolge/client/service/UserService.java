package com.yolge.client.service;

import com.yolge.client.core.RestClient;
import com.yolge.client.dto.PageResponse;
import com.yolge.client.dto.user.RegisterRequest;
import com.yolge.client.dto.user.DeleteUserResponse;
import com.yolge.client.dto.user.UpdateUserRequest;
import com.yolge.client.dto.user.UpdateUserResponse;
import com.yolge.client.dto.user.UserResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UserService {

    private static UserService instance;
    private final RestClient restClient;

    private UserService() {
        this.restClient = RestClient.getInstance();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public PageResponse<UserResponse> getAll(int page, int size) {
        String endpoint = String.format("/users?page=%d&size=%d", page, size);
        return restClient.getPage(endpoint, UserResponse.class);
    }

    public PageResponse<UserResponse> searchByUsername(int page, int size, String username) {
        String encoded = URLEncoder.encode(username, StandardCharsets.UTF_8);
        String endpoint = String.format("/users?page=%d&size=%d&username=%s", page, size, encoded);
        return restClient.getPage(endpoint, UserResponse.class);
    }

    public UserResponse getById(Long id) {
        return restClient.get("/users/" + id, UserResponse.class);
    }

    public void createUser(RegisterRequest request) {
        restClient.post("/auth/register", request, UserResponse.class);
    }

    public void updateUser(Long id, UpdateUserRequest request) {
        restClient.put("/users/" + id, request, UpdateUserResponse.class);
    }

    public void deleteUser(Long id) {
        restClient.delete("/users/" + id, DeleteUserResponse.class);
    }
}