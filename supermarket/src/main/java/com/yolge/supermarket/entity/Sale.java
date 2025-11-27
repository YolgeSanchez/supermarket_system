package com.yolge.supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yolge.supermarket.enums.SaleStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@Entity
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double totalPrice;
    private LocalDateTime finishedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private SaleStatus status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "cashier_id")
    private User cashier;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("sale")
    private List<SaleDetail> saleDetails = new ArrayList<>();

    public void finishSale() {
        this.status = SaleStatus.CLOSED;
        this.finishedAt = LocalDateTime.now();
    }

    public void cancelSale() {
        this.status = SaleStatus.CANCELED;
        this.finishedAt = LocalDateTime.now();
    }

    private void calculateTotalPrice() {
        totalPrice = saleDetails.stream()
                .mapToDouble(SaleDetail::getSubTotal)
                .sum();
    }

    public void addSaleDetail(SaleDetail saleDetail) {
        saleDetails.add(saleDetail);
        saleDetail.setSale(this);
        calculateTotalPrice();
    }

    public void removeSaleDetail(SaleDetail saleDetail) {
        saleDetails.remove(saleDetail);
        saleDetail.setSale(null);
        calculateTotalPrice();
    }
}
