package com.yolge.supermarket.service.promotion;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.promotion.DeletePromotionResponse;
import com.yolge.supermarket.dto.promotion.PromotionRequest;
import com.yolge.supermarket.dto.promotion.PromotionResponse;
import com.yolge.supermarket.entity.Product;
import com.yolge.supermarket.entity.Promotion;
import com.yolge.supermarket.exceptions.NotFoundException;
import com.yolge.supermarket.mapper.PageMapper;
import com.yolge.supermarket.mapper.PromotionMapper;
import com.yolge.supermarket.repository.ProductRepository;
import com.yolge.supermarket.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionServiceImpl implements PromotionService{

    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;
    private final PromotionMapper promotionMapper;
    private final PageMapper pageMapper;

    @Override
    @Transactional
    public PromotionResponse createPromotion(PromotionRequest request) {
        List<Product> products = this.findProductsByIds(request.getProductIds());

        Promotion promotion = promotionMapper.toEntity(request, products);
        Promotion savedPromotion = promotionRepository.save(promotion);

        return promotionMapper.toDto(savedPromotion);
    }

    @Override
    @Transactional
    public DeletePromotionResponse deleteById(Long id) {
        Promotion promotion = this.getByIdEntity(id);
        promotion.softDelete();

        return new DeletePromotionResponse("Promoción eliminada correctamente.", id);
    }

    @Override
    public PromotionResponse getById(Long id) {
        Promotion promotion = promotionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Promoción no encontrada o inactiva!"));

        return promotionMapper.toDto(promotion);
    }

    @Override
    public PageResponse<PromotionResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Promotion> promotions = promotionRepository.findAllByDeletedAtIsNull(pageable);
        List<PromotionResponse> promotionsResponses = promotionMapper.toDtoList(promotions.getContent());

        return pageMapper.toDto(promotionsResponses, promotions);
    }

    @Override
    public PageResponse<PromotionResponse> searchByName(int page, int size, String name) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Promotion> promotions = promotionRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(pageable, name);
        List<PromotionResponse> promotionsResponses = promotionMapper.toDtoList(promotions.getContent());

        return pageMapper.toDto(promotionsResponses, promotions);
    }

    private Promotion getByIdEntity(Long id) {
        return promotionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Promoción no encontrada!"));
    }

    private List<Product> findProductsByIds(Long[] ids) {
        if (ids == null || ids.length == 0) return new ArrayList<>();

        return productRepository.findAllById(Arrays.asList(ids));
    }
}
