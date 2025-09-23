package org.shopme.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "shipping_rate")
@AllArgsConstructor
@NoArgsConstructor
public class ShippingRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @PositiveOrZero
    private float rate;

    @PositiveOrZero
    private int days;

    @Column(name = "cod_supported")
    private boolean codSupported;

    @Column(nullable = false, length = 45)
    private String country;

    @Column(nullable = false, length = 45)
    private String state;
}
