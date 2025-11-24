package com.yolge.supermarket.service.product;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.product.DeleteProductResponse;
import com.yolge.supermarket.dto.product.ProductRequest;
import com.yolge.supermarket.dto.product.ProductResponse;
import com.yolge.supermarket.entity.Category;
import com.yolge.supermarket.entity.Product;
import com.yolge.supermarket.exceptions.ConflictException;
import com.yolge.supermarket.exceptions.NotFoundException;
import com.yolge.supermarket.mapper.PageMapper;
import com.yolge.supermarket.mapper.ProductMapper;
import com.yolge.supermarket.repository.CategoryRepository;
import com.yolge.supermarket.repository.ProductRepository;
import com.yolge.supermarket.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PromotionRepository promotionRepository;
    private final ProductMapper productMapper;
    private final PageMapper pageMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Categoria no encontrada!"));

        if (productRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(request.getName())) {
            throw new ConflictException("Ya existe un producto con ese nombre!");
        }

        Product product = productMapper.toEntity(request);
        category.addProduct(product);
        Product savedProduct = productRepository.save(product);

        return productMapper.toDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateById(Long id, ProductRequest request) {
        Product product = this.getByIdEntity(id);
        Long currCategoryId = product.getCategory().getId();
        Long newCategoryId = request.getCategoryId();

        if (!currCategoryId.equals(newCategoryId)) {
            Category currCategory = categoryRepository.findByIdAndDeletedAtIsNull(currCategoryId)
                    .orElseThrow(() -> new NotFoundException("Categoria no encontrada!"));
            Category newCategory = categoryRepository.findByIdAndDeletedAtIsNull(newCategoryId)
                    .orElseThrow(() -> new NotFoundException("Categoria no encontrada!"));

            currCategory.removeProduct(product);
            newCategory.addProduct(product);
        }

        productMapper.updateEntity(product, request);
        Product updatedProduct = productRepository.save(product);

        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional
    public DeleteProductResponse deleteById(Long id) {
        this.getByIdEntity(id).softDelete();
        return new DeleteProductResponse("El producto ha sido eliminado correctamente!", id);
    }

    @Override
    public PageResponse<ProductResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products = productRepository.findAllByDeletedAtIsNull(pageable);
        List<ProductResponse> productResponses = (List<ProductResponse>) productMapper.toDtoList(products.getContent());

        return pageMapper.toDto(productResponses, products);
    }

    @Override
    public ProductResponse getById(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado!"));

        Double discount = promotionRepository.findMaxDiscountForProduct(id, LocalDateTime.now());
        return productMapper.toDto(product, discount);
    }

    @Override
    public PageResponse<ProductResponse> searchByCategoryId(int page, int size, Long categoryId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products = productRepository.findAllByCategoryIdAndDeletedAtIsNull(pageable, categoryId);
        List<ProductResponse> productResponses = (List<ProductResponse>) productMapper.toDtoList(products.getContent());

        return pageMapper.toDto(productResponses, products);
    }

    @Override
    public PageResponse<ProductResponse> searchByName(int page, int size, String name) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products = productRepository.findAllByNameContainingIgnoreCaseAndDeletedAtIsNull(pageable, name);
        List<ProductResponse> productResponses = (List<ProductResponse>) productMapper.toDtoList(products.getContent());

        return pageMapper.toDto(productResponses, products);
    }

    @Override
    public PageResponse<ProductResponse> searchByBrand(int page, int size, String brand) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products = productRepository.findAllByBrandContainingIgnoreCaseAndDeletedAtIsNull(pageable, brand);
        List<ProductResponse> productResponses = (List<ProductResponse>) productMapper.toDtoList(products.getContent());

        return pageMapper.toDto(productResponses, products);
    }

    @Override
    public PageResponse<ProductResponse> searchByCategoryName(int page, int size, String categoryName) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products = productRepository.findAllByCategoryNameContainingIgnoreCaseAndDeletedAtIsNull(pageable, categoryName);
        List<ProductResponse> productResponses = (List<ProductResponse>) productMapper.toDtoList(products.getContent());

        return pageMapper.toDto(productResponses, products);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void decreaseStockById(Long id, int quantity) {
        Product product = this.getByIdEntity(id);
        product.unstock(quantity);
    }

    @Override
    @Transactional
    public ProductResponse increaseStockById(Long id, int quantity) {
        Product product = this.getByIdEntity(id);
        product.restock(quantity);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    private Product getByIdEntity(Long id) {
        return productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado!"));
    }
}
