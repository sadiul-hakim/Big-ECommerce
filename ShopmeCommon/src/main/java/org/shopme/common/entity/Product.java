package org.shopme.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 200, unique = true, nullable = false)
    private String name;

    @Column(length = 200, unique = true, nullable = false)
    private String alias;

    @Column(length = 500)
    private String shortDescription;

    @Column(length = 2500)
    private String fullDescription;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedTime;

    private boolean enabled;
    private boolean inStock;

    private float cost;

    private float price;
    private float discountPrice;

    private float length;
    private float weight;
    private float height;
    private float width;

    private String category;
    private String brand;

    @Transient
    private Category categoryObject;

    @Transient
    private Brand brandObject;
}
