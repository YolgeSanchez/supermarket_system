package com.yolge.supermarket.mapper;

import com.yolge.supermarket.dto.client.ClientRequest;
import com.yolge.supermarket.dto.client.ClientResponse;
import com.yolge.supermarket.entity.Client;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientMapper {
    public Client toEntity(ClientRequest request) {
        Client client = new Client();
        client.setName(request.getName());
        client.setDni(request.getDni());
        client.setEmail(request.getEmail());
        return client;
    }

    public ClientResponse toDto(Client client) {
        if (client == null) return null;
        ClientResponse dto = new ClientResponse();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setDni(client.getDni());
        dto.setEmail(client.getEmail());
        dto.setCreatedAt(client.getCreatedAt());
        dto.setUpdatedAt(client.getUpdatedAt());
        return dto;
    }

    public List<ClientResponse> toDtoList(List<Client> clients) {
        return clients.stream().map(this::toDto).collect(Collectors.toList());
    }
}
