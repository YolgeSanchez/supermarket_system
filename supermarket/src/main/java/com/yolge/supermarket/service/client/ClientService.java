package com.yolge.supermarket.service.client;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.client.ClientRequest;
import com.yolge.supermarket.dto.client.ClientResponse;
import com.yolge.supermarket.dto.client.DeleteClientResponse;
import org.springframework.stereotype.Service;

@Service
public interface ClientService {
    ClientResponse createClient(ClientRequest request);
    ClientResponse updateById(Long id, ClientRequest request);
    DeleteClientResponse deleteById(Long id);
    PageResponse<ClientResponse> getAll(int page, int size);
    ClientResponse getById(Long id);
    ClientResponse getByDni(String dni);
    PageResponse<ClientResponse> searchByName(int page, int size, String name);
    PageResponse<ClientResponse> searchByEmail(int page, int size, String email);
}
