package org.shopme.site.cart;

import org.shopme.common.entity.CartItem;
import org.shopme.common.entity.Customer;
import org.shopme.common.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCustomerAndProduct(Customer customer, Product product);
    List<CartItem> findAllByCustomer(Customer customer);

    void deleteAllByCustomer(Customer customer);
}
