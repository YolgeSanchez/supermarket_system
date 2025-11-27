package com.yolge.supermarket.service.client;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.client.ClientRequest;
import com.yolge.supermarket.dto.client.ClientResponse;
import com.yolge.supermarket.dto.client.DeleteClientResponse;
import com.yolge.supermarket.entity.Client;
import com.yolge.supermarket.exceptions.ConflictException;
import com.yolge.supermarket.exceptions.NotFoundException;
import com.yolge.supermarket.mapper.ClientMapper;
import com.yolge.supermarket.mapper.PageMapper;
import com.yolge.supermarket.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final PageMapper pageMapper;

    @Override
    @Transactional
    public ClientResponse createClient(ClientRequest request) {
        if (clientRepository.existsByDniAndDeletedAtIsNull(request.getDni())) {
            throw new ConflictException("Ya existe un cliente con el DNI: " + request.getDni());
        }
        if (clientRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new ConflictException("Ya existe un cliente con el Email: " + request.getEmail());
        }

        Client client = clientMapper.toEntity(request);
        return clientMapper.toDto(clientRepository.save(client));
    }

    @Override
    @Transactional
    public ClientResponse updateById(Long id, ClientRequest request) {
        Client client = clientRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

        if (!client.getDni().equals(request.getDni()) &&
                clientRepository.existsByDniAndDeletedAtIsNull(request.getDni())) {
            throw new ConflictException("El DNI ya está en uso");
        }

        if (!client.getEmail().equals(request.getEmail()) &&
                clientRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new ConflictException("El Email ya está en uso");
        }

        client.setName(request.getName());
        client.setDni(request.getDni());
        client.setEmail(request.getEmail());

        return clientMapper.toDto(clientRepository.save(client));
    }

    @Override
    @Transactional
    public DeleteClientResponse deleteById(Long id) {
        Client client = clientRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado!"));

        client.softDelete();
        return new DeleteClientResponse("Cliente eliminado correctamente", id);
    }

    @Override
    public PageResponse<ClientResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").descending());
        Page<Client> clients = clientRepository.findAllByDeletedAtIsNull(pageable);
        return pageMapper.toDto(clientMapper.toDtoList(clients.getContent()), clients);
    }

    @Override
    public ClientResponse getById(Long id) {
        return clientRepository.findByIdAndDeletedAtIsNull(id)
                .map(clientMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado!"));
    }

    @Override
    public ClientResponse getByDni(String dni) {
        return clientRepository.findByDniAndDeletedAtIsNull(dni)
                .map(clientMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado con DNI: " + dni));
    }

    @Override
    public PageResponse<ClientResponse> searchByName(int page, int size, String name) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Client> clients = clientRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(pageable, name);
        return pageMapper.toDto(clientMapper.toDtoList(clients.getContent()), clients);
    }

    @Override
    public PageResponse<ClientResponse> searchByEmail(int page, int size, String email) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Client> clients = clientRepository.findByEmailContainingIgnoreCaseAndDeletedAtIsNull(pageable, email);
        return pageMapper.toDto(clientMapper.toDtoList(clients.getContent()), clients);
    }
}