package com.yolge.supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yolge.supermarket.exceptions.ConflictException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String brand;
    private Double basePrice;
    private Double taxPercentage;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties("products")
    private Category category;
    private Integer stock;

    @ManyToMany(mappedBy = "products")
    @JsonIgnore
    private List<Promotion> promotions = new ArrayList<>();
    private LocalDateTime deletedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Double getProductTotalPrice() {
        return basePrice + (basePrice * taxPercentage / 100);
    }

    public Double getProductTotalPrice(Double discountPercentage) {
        Double newBasePrice = basePrice * (1 - discountPercentage / 100);
        return newBasePrice + (newBasePrice * taxPercentage / 100);
    }

    public Boolean isInStock(int qty) {
        return this.stock >= qty;
    }

    public void unstock(int qty) {
        if (!isInStock(qty)) {
            throw new ConflictException("Stock insuficiente para el producto: " + this.name);
        }
        this.stock -= qty;
    }

    public void restock(int qty) {
        this.stock += qty;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
