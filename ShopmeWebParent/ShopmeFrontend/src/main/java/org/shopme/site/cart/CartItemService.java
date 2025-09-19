package org.shopme.site.cart;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.CartItem;
import org.shopme.common.entity.Customer;
import org.shopme.common.entity.Product;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.NumberFormatter;
import org.shopme.site.customer.CustomerService;
import org.shopme.site.product.ProductService;
import org.shopme.site.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final ProductService productService;
    private final CustomerService customerService;
    private final CartItemRepository repository;

    @Transactional
    public Map<String, Object> incrementQuantity(long cartItemId, NumberFormatter numberFormatter) {

        CartItem cartItem = repository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find cart item with id " + cartItemId));
        int item = cartItem.getQuantity() + 1;
        cartItem.setQuantity(item);

        var price = cartItem.getQuantity() * cartItem.getProduct().getDiscountPrice();
        return Map.of("quantity", cartItem.getQuantity(), "totalPrice",
                numberFormatter.format(price));
    }

    @Transactional
    public Map<String, Object> decrementQuantity(long cartItemId, NumberFormatter numberFormatter) {

        CartItem cartItem = repository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find cart item with id " + cartItemId));

        if (cartItem.getQuantity() <= 1) {
            deleteCartItem(cartItemId);
            return Map.of("quantity", 0, "totalPrice", 0);
        }

        int item = cartItem.getQuantity() - 1;
        cartItem.setQuantity(item);

        var price = cartItem.getQuantity() * cartItem.getProduct().getDiscountPrice();
        return Map.of("quantity", cartItem.getQuantity(), "totalPrice",
                numberFormatter.format(price));
    }

    @Transactional
    public JpaResult addToCart(int productId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        Customer customer = principal.customer();
        Optional<Product> productOpt = productService.findById(productId);
        if (productOpt.isEmpty()) {
            return new JpaResult(JpaResultType.FAILED, "Customer or Product does not exist.");
        }

        Product product = productOpt.get();
        CartItem cartItem = repository.findByCustomerAndProduct(customer, product)
                .orElse(new CartItem(0, customer, product, 0));

        cartItem.setQuantity(cartItem.getQuantity() + 1);
        repository.save(cartItem);

        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully added product to cart.");
    }


    public CartItem findItem(int customerId, int productId) throws EntityNotFoundException {

        Customer customer = customerService.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find customer with id " + customerId));
        Product product = productService.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find product with id " + productId));

        return repository.findByCustomerAndProduct(customer, product)
                .orElseThrow(
                        () -> new EntityNotFoundException("Could not find cart item with customer id "
                                + customerId + " product id " + productId)
                );
    }

    public List<CartItem> findAllCartItemOfCustomer() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        Customer customer = principal.customer();
        return repository.findAllByCustomer(customer);
    }

    public boolean deleteCartItem(long cartItemId) {

        try {
            repository.deleteById(cartItemId);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
