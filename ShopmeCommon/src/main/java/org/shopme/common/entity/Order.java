package org.shopme.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.shopme.common.enumeration.OrderStatus;
import org.shopme.common.enumeration.PaymentMethod;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Size(max = 45)
    @Column(length = 45, nullable = false)
    private String firstName;

    @NotNull
    @Size(max = 45)
    @Column(length = 45, nullable = false)
    private String lastName;

    // The Address of the customer and the address of the order is not the same.
    // Because a Customer can change his address anytime, that would change the order address.\
    // So, we have to keep static address in the order table even though they are duplicates.

    @NotNull
    @NotEmpty
    @Size(min = 11, max = 15)
    @Column(length = 15, nullable = false)
    private String phoneNumber;

    @NotNull
    @NotEmpty
    @Size(min = 11, max = 15)
    @Column(length = 15)
    private String alternativePhoneNumber;

    @NotNull
    @NotEmpty
    @Size(max = 150)
    @Column(length = 150, nullable = false)
    private String address;

    @NotNull
    @Size(max = 45)
    @Column(length = 45, nullable = false)
    private String country;

    @NotNull
    @Size(max = 45)
    @Column(length = 45, nullable = false)
    private String state;

    @NotNull
    @Size(max = 45)
    @Column(length = 45, nullable = false)
    private String city;

    @NotNull
    @NotEmpty
    @Size(max = 10)
    @Column(length = 10, nullable = false)
    private String postalCode;

    private LocalDateTime orderTime;
    private float shippingCost;
    private float productCost;
    private float subtotal;
    private float tax;
    private float total;

    private int deliveryDays;
    private LocalDateTime deliveryDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<OrderDetails> details = new HashSet<>();

    public void addDetails(OrderDetails details) {
        this.details.add(details);
        details.setOrder(this);
    }

    public void removeDetails(OrderDetails details) {
        details.setOrder(null);
        this.details.remove(details);
    }

    public void removeAllDetails() {
        Iterator<OrderDetails> iterator = this.details.iterator();
        while (iterator.hasNext()) {
            OrderDetails orderDetails = iterator.next();
            orderDetails.setOrder(null);
            iterator.remove();
        }
    }
}
