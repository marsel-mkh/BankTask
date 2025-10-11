package com.t1.marselmkh.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "product_seq", allocationSize = 1)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductKey key;

    private LocalDate createDate;

    @Column(nullable = false, unique = true)
    private String productId;

    @PrePersist
    public void prePersist() {
        if (productId == null) {
            this.productId = key + "" + id;
        }
    }
}