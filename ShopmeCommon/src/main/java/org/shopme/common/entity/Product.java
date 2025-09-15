package org.shopme.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.shopme.common.converter.StringArrayConverter;
import org.shopme.common.converter.StringMapConverter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    @Column(length = 1500)
    private String shortDescription;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String fullDescription;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedTime;

    private boolean enabled;

    private int quantity;

    private float cost;

    private float price;
    private float discountPrice;

    private float length;
    private float weight;
    private float height;
    private float width;

    private String category;
    private String brand;

    @Convert(converter = StringArrayConverter.class)
    @Column(columnDefinition = "JSON")
    private String[] files = new String[4];

    @Convert(converter = StringMapConverter.class)
    @Column(columnDefinition = "JSON")
    private Map<String, String> details = new HashMap<>();

    @Transient
    private String image;
}
