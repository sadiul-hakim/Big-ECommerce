package org.shopme.site.order;

import org.shopme.common.entity.Order;
import org.shopme.common.enumeration.OrderStatus;
import org.shopme.common.enumeration.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
