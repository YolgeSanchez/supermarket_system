package com.yolge.supermarket.service.sale;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.sale.SaleResponse;
import com.yolge.supermarket.entity.*;
import com.yolge.supermarket.enums.SaleStatus;
import com.yolge.supermarket.exceptions.BadRequestException;
import com.yolge.supermarket.exceptions.ConflictException;
import com.yolge.supermarket.exceptions.NotFoundException;
import com.yolge.supermarket.mapper.PageMapper;
import com.yolge.supermarket.mapper.SaleMapper;
import com.yolge.supermarket.repository.ClientRepository;
import com.yolge.supermarket.repository.SaleRepository;
import com.yolge.supermarket.repository.UserRepository;
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
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final SaleMapper saleMapper;
    private final PageMapper pageMapper;

    @Override
    @Transactional
    public SaleResponse createSale(Long clientId, Long cashierId) {
        Sale sale = new Sale();

        if (clientId != null) {
            Client client = clientRepository.findByIdAndDeletedAtIsNull(clientId)
                    .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
            sale.setClient(client);
        }

        User cashier = userRepository.findById(cashierId)
                .orElseThrow(() -> new NotFoundException("Cajero no encontrado!"));
        sale.setCashier(cashier);

        sale.setStatus(SaleStatus.OPEN);
        sale.setTotalPrice(0.0);

        return saleMapper.toDto(saleRepository.save(sale));
    }

    @Override
    public SaleResponse getById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venta no encontrada!"));
        return saleMapper.toDto(sale);
    }

    @Override
    @Transactional
    public SaleResponse finishSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venta no encontrada!"));

        if (sale.getStatus() != SaleStatus.OPEN) {
            throw new ConflictException("La venta no está abierta (ya fue cerrada o cancelada).");
        }

        if (sale.getSaleDetails().isEmpty()) {
            throw new ConflictException("No se puede finalizar una venta vacía.");
        }

        sale.finishSale();

        return saleMapper.toDto(saleRepository.save(sale));
    }

    @Override
    @Transactional
    public SaleResponse cancelSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venta no encontrada"));

        if (sale.getStatus() == SaleStatus.CANCELED) {
            throw new ConflictException("La venta ya está cancelada.");
        }

        for (SaleDetail detail : sale.getSaleDetails()) {
            Product product = detail.getProduct();
            product.restock(detail.getQuantity());
        }

        sale.cancelSale();

        return saleMapper.toDto(saleRepository.save(sale));
    }

    @Override
    public PageResponse<SaleResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Sale> sales = saleRepository.findAll(pageable);
        return pageMapper.toDto(saleMapper.toDtoList(sales.getContent()), sales);
    }

    @Override
    public PageResponse<SaleResponse> getByClient(int page, int size, Long clientId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Sale> sales = saleRepository.findAllByClientId(pageable, clientId);
        return pageMapper.toDto(saleMapper.toDtoList(sales.getContent()), sales);
    }

    @Override
    public PageResponse<SaleResponse> getByCashier(int page, int size, Long cashierId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Sale> sales = saleRepository.findAllByCashierId(pageable, cashierId);
        return pageMapper.toDto(saleMapper.toDtoList(sales.getContent()), sales);
    }

    @Override
    public PageResponse<SaleResponse> getBySaleStatus(int page, int size, String status) {
        SaleStatus saleStatus;
        try {
            saleStatus = SaleStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Estado de compra inválido!");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Sale> sales = saleRepository.findAllByStatus(pageable, saleStatus);
        return pageMapper.toDto(saleMapper.toDtoList(sales.getContent()), sales);
    }
}