package org.shopme.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "customer_address")
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Customer customer;

    @NotNull
    @NotEmpty
    @Size(min = 11, max = 15)
    @Column(length = 15, nullable = false, unique = true)
    private String phoneNumber;

    @NotNull
    @NotEmpty
    @Size(max = 150)
    @Column(length = 150, nullable = false)
    private String address;

    @NotNull
    @ManyToOne
    private Country country;

    @NotNull
    @ManyToOne
    private State state;

    @NotNull
    @NotEmpty
    @Size(max = 10)
    @Column(length = 10, nullable = false)
    private String postalCode;

    private boolean selected;

    public Address(Customer customer, String phoneNumber, String address, Country country, State state, String postalCode,
                   boolean selected) {
        this.customer = customer;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.country = country;
        this.state = state;
        this.postalCode = postalCode;
        this.selected = selected;
    }
}
