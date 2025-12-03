package com.yolge.client.service;

import com.yolge.client.core.RestClient;
import com.yolge.client.dto.PageResponse;
import com.yolge.client.dto.client.ClientRequest;
import com.yolge.client.dto.client.ClientResponse;
import com.yolge.client.dto.client.DeleteClientResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ClientService {

    private static ClientService instance;
    private final RestClient restClient;

    private ClientService() {
        this.restClient = RestClient.getInstance();
    }

    public static synchronized ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
        }
        return instance;
    }

    public PageResponse<ClientResponse> getAll(int page, int size) {
        String endpoint = String.format("/clients?page=%d&size=%d", page, size);
        return restClient.getPage(endpoint, ClientResponse.class);
    }

    public PageResponse<ClientResponse> searchByName(int page, int size, String name) {
        String encoded = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String endpoint = String.format("/clients?page=%d&size=%d&name=%s", page, size, encoded);
        return restClient.getPage(endpoint, ClientResponse.class);
    }

    public PageResponse<ClientResponse> searchByEmail(int page, int size, String email) {
        String encoded = URLEncoder.encode(email, StandardCharsets.UTF_8);
        String endpoint = String.format("/clients?page=%d&size=%d&email=%s", page, size, encoded);
        return restClient.getPage(endpoint, ClientResponse.class);
    }

    public ClientResponse getById(Long id) {
        return restClient.get("/clients/" + id, ClientResponse.class);
    }

    public void createClient(ClientRequest request) {
        restClient.post("/clients", request, ClientResponse.class);
    }

    public void updateClient(Long id, ClientRequest request) {
        restClient.put("/clients/" + id, request, ClientResponse.class);
    }

    public void deleteClient(Long id) {
        restClient.delete("/clients/" + id, DeleteClientResponse.class);
    }
}