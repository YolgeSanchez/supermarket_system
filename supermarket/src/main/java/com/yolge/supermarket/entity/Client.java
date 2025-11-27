package com.yolge.supermarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String dni;
    private String email;
    private LocalDateTime deletedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "client")
    @SQLRestriction("status IS NOT 'CANCELLED'")
    @JsonIgnoreProperties("client")
    private List<Sale> sales = new ArrayList<>();

    private void addSale(Sale sale) {
        sales.add(sale);
        sale.setClient(this);
    }

    private void removeSale(Sale sale) {
        sales.remove(sale);
        sale.setClient(null);
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
