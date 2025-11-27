package com.yolge.supermarket.service.saleDetail;

import com.yolge.supermarket.dto.saleDetail.SaleDetailRequest;
import com.yolge.supermarket.entity.*;
import com.yolge.supermarket.enums.SaleStatus;
import com.yolge.supermarket.exceptions.ConflictException;
import com.yolge.supermarket.exceptions.NotFoundException;
import com.yolge.supermarket.repository.ProductRepository;
import com.yolge.supermarket.repository.PromotionRepository;
import com.yolge.supermarket.repository.SaleDetailRepository;
import com.yolge.supermarket.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleDetailServiceImpl implements SaleDetailService {

    private final SaleDetailRepository saleDetailRepository;
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;

    @Override
    public SaleDetail CreateDetail(Long saleId, SaleDetailRequest request) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new NotFoundException("Venta no encontrada!"));

        if (sale.getStatus() != SaleStatus.OPEN) {
            throw new ConflictException("No se pueden agregar productos a una venta cerrada.");
        }

        Product product = productRepository.findByIdAndDeletedAtIsNull(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Producto no encontrado!"));

        product.unstock(request.getQuantity());

        Double maxDiscount = promotionRepository.findMaxDiscountForProduct(product.getId(), LocalDateTime.now());
        Double finalPrice = product.getProductTotalPrice(maxDiscount);

        SaleDetail detail = new SaleDetail();
        detail.setProduct(product);
        detail.setQuantity(request.getQuantity());
        detail.setUnitPrice(finalPrice);
        detail.calculateSubTotal();

        sale.addSaleDetail(detail);

        return saleDetailRepository.save(detail);
    }

    @Override
    public SaleDetail UpdateById(Long id, Integer newQty) {
        SaleDetail detail = saleDetailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Detalle no encontrado!"));

        Sale sale = detail.getSale();
        if (sale.getStatus() != SaleStatus.OPEN) {
            throw new ConflictException("No se puede modificar una venta cerrada.");
        }

        Product product = detail.getProduct();
        int diff = newQty - detail.getQuantity();

        if (diff > 0) product.unstock(diff);
        else if (diff < 0) product.restock(Math.abs(diff));

        detail.setQuantity(newQty);
        detail.calculateSubTotal();

        sale.removeSaleDetail(detail);
        sale.addSaleDetail(detail);

        return detail;
    }

    @Override
    public void removeById(Long id) {
        SaleDetail detail = saleDetailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Detalle de compra no encontrado!"));

        Sale sale = detail.getSale();
        if (sale.getStatus() != SaleStatus.OPEN) {
            throw new ConflictException("No se puede modificar una venta cerrada.");
        }

        Product product = detail.getProduct();

        product.restock(detail.getQuantity());

        sale.removeSaleDetail(detail);

        saleDetailRepository.delete(detail);
    }

    @Override
    public SaleDetail getById(Long id) {
        return saleDetailRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Detalle no encontrado"));
    }
}