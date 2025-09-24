package org.shopme.admin.order;

import org.shopme.common.entity.Order;
import org.shopme.common.enumeration.OrderStatus;
import org.shopme.common.enumeration.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
                 SELECT o FROM Order o
                 WHERE (:keyword IS NULL OR\s
                        o.firstName LIKE %:keyword% OR\s
                        o.lastName LIKE %:keyword% OR\s
                        o.phoneNumber LIKE %:keyword% OR\s
                        o.address LIKE %:keyword% OR\s
                        o.country LIKE %:keyword% OR\s
                        o.state LIKE %:keyword% OR\s
                        o.city LIKE %:keyword% OR\s
                        o.postalCode LIKE %:keyword%)
                    AND (:paymentMethod IS NULL OR o.paymentMethod = :paymentMethod)
                    AND (:statusEnum IS NULL OR o.status = :statusEnum)
            \s""")
    Page<Order> searchOrders(@Param("keyword") String keyword,
                             @Param("paymentMethod") PaymentMethod paymentMethod,
                             @Param("statusEnum") OrderStatus status,
                             Pageable pageable);
}
