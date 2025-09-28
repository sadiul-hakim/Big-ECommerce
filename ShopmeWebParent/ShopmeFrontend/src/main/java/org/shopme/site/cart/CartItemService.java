package org.shopme.site.cart;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.*;
import org.shopme.common.util.CurrencySettingBag;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.NumberFormatter;
import org.shopme.site.currency.CurrencyService;
import org.shopme.site.customer.CustomerService;
import org.shopme.site.product.ProductService;
import org.shopme.site.security.CustomUserDetails;
import org.shopme.site.setting.SettingService;
import org.shopme.site.shipping.ShippingRateService;
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
    private final ShippingRateService shippingRateService;
    private final SettingService settingService;
    private final CurrencyService currencyService;

    @Transactional
    public Map<String, Object> updateQuantity(long cartItemId, int quantity, boolean replace,
                                              NumberFormatter numberFormatter) {

        Optional<CartItem> cartItemOpt = repository.findById(cartItemId);
        if (cartItemOpt.isEmpty()) {
            return Map.of();
        }
        CartItem cartItem = cartItemOpt.get();
        if (replace) {
            cartItem.setQuantity(quantity);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        var price = cartItem.getQuantity() * cartItem.getProduct().getDiscountPrice();
        var actualPrice = cartItem.getQuantity() * cartItem.getProduct().getPrice();
        double estimatedTotalPrices = getTotalItemsPriceOfCustomer();
        double paymentTotal = getPaymentTotal();

        return Map.of(
                "quantity", cartItem.getQuantity(),
                "totalPrice", numberFormatter.format(price),
                "actualPrice", numberFormatter.format(actualPrice),
                "estimatedTotalPrices", numberFormatter.format(estimatedTotalPrices),
                "paymentTotal", numberFormatter.format(paymentTotal)
        );
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

        CurrencySettingBag currencySettingBag = settingService.getCurrencySettingBag();
        String currencyId = currencySettingBag.getCurrencyId();

        Currency currency = null;
        if (currencyId != null) {
            currency = currencyService.findById(Long.parseLong(currencyId));
        }

        if (currency == null) {
            return new JpaResult(JpaResultType.FAILED, "No selected currency.");
        }

        Product product = productOpt.get();
        CartItem cartItem = repository.findByCustomerAndProduct(customer, product)
                .orElse(new CartItem(0, customer, product, currency.getCode(), 0));

        cartItem.setQuantity(cartItem.getQuantity() + 1);
        repository.save(cartItem);

        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully added product to cart.");
    }


    public CartItem findItem(int customerId, int productId) throws EntityNotFoundException {

        Optional<Customer> customer = customerService.findById(customerId);
        if (customer.isEmpty()) {
            return null;
        }
        Optional<Product> product = productService.findById(productId);
        if (product.isEmpty()) {
            return null;
        }

        return repository.findByCustomerAndProduct(customer.get(), product.get()).orElse(null);
    }

    public List<CartItem> findAllCartItemOfCustomer() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        Customer customer = principal.customer();
        return repository.findAllByCustomer(customer);
    }

    public double getTotalItemsPriceOfCustomer() {
        List<CartItem> items = findAllCartItemOfCustomer();
        return items.stream().map(CartItem::getTotalPrice).reduce(0.0, Double::sum);
    }

    public double getTotalItemsPriceOfCustomer(List<CartItem> items) {
        return items.stream().map(CartItem::getTotalPrice).reduce(0.0, Double::sum);
    }

    public double getPaymentTotal(List<CartItem> items) {
        double totalItemsPrice = getTotalItemsPriceOfCustomer(items);
        Optional<ShippingRate> shippingRateOptional = shippingRateService.currentCustomerShipping();
        if (shippingRateOptional.isEmpty()) {
            return totalItemsPrice;
        }

        ShippingRate shippingRate = shippingRateOptional.get();
        return totalItemsPrice + shippingRate.getRate();
    }

    public double getPaymentTotal() {
        double totalItemsPrice = getTotalItemsPriceOfCustomer();
        Optional<ShippingRate> shippingRateOptional = shippingRateService.currentCustomerShipping();
        if (shippingRateOptional.isEmpty()) {
            return totalItemsPrice;
        }

        ShippingRate shippingRate = shippingRateOptional.get();
        return totalItemsPrice + shippingRate.getRate();
    }

    public boolean deleteCartItem(long cartItemId) {

        try {
            repository.deleteById(cartItemId);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean emptyCartOfCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        Customer customer = principal.customer();
        repository.deleteAllByCustomer(customer);

        return true;
    }

    public boolean isInCart(int productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        Customer customer = principal.customer();

        CartItem item = findItem(customer.getId(), productId);
        return item != null;
    }
}
