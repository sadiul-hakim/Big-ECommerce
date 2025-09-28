package org.shopme.site.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.*;
import org.shopme.common.enumeration.OrderStatus;
import org.shopme.common.enumeration.PaymentMethod;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.util.CurrencySettingBag;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.PageUtil;
import org.shopme.site.address.AddressService;
import org.shopme.site.cart.CartItemRepository;
import org.shopme.site.cart.CartItemService;
import org.shopme.site.currency.CurrencyService;
import org.shopme.site.security.CustomUserDetails;
import org.shopme.site.setting.SettingService;
import org.shopme.site.shipping.ShippingRateService;
import org.shopme.site.util.OrderIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final CartItemService cartItemService;
    private final AddressService addressService;
    private final ShippingRateService shippingRateService;

    public Order findById(long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public JpaResult placeOrderOfCurrentUser(PaymentMethod method) {

        List<CartItem> items = cartItemService.findAllCartItemOfCustomer();
        Address address = addressService.currentCustomerActiveAddress();
        Optional<ShippingRate> shippingRateOptional = shippingRateService.currentCustomerShipping();
        if (shippingRateOptional.isEmpty()) {
            return new JpaResult(JpaResultType.FAILED, "No shipping available");
        }

        ShippingRate shippingRate = shippingRateOptional.get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Customer customer = userDetails.customer();

        float cost = items.stream().map(item -> item.getProduct().getCost()).reduce(0.0f, Float::sum);
        Float price = items.stream().map(item -> item.getProduct().getDiscountPrice()).reduce(0.0f, Float::sum);
        float subTotal = price + shippingRate.getRate();
        float total = subTotal + 0; // subtotal + tax

        Order order = new Order();
        order.setId(OrderIdGenerator.generateOrderId());
        order.setFirstName(customer.getFullName());
        order.setLastName(customer.getLastname());
        order.setCustomer(customer);
        order.setPhoneNumber(address.getPhoneNumber());
        order.setAlternativePhoneNumber(address.getAlternativePhoneNumber());
        order.setAddress(address.getAddress());
        order.setCountry(address.getCountry().getName());
        order.setState(address.getState().getName());
        order.setCity(address.getCity());
        order.setPostalCode(address.getPostalCode());
        order.setOrderTime(LocalDateTime.now());
        order.setShippingCost(shippingRate.getRate());
        order.setProductCost(cost);
        order.setSubtotal(subTotal);
        order.setTax(0);
        order.setTotal(total);
        order.setDeliveryDays(shippingRate.getDays());
        order.setDeliveryDate(LocalDateTime.now().plusDays(shippingRate.getDays()));
        order.setPaymentMethod(method);
        order.setStatus(OrderStatus.NEW);

        // Create details and call order.addDetails to make relation

        for (CartItem item : items) {
            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setQuantity(item.getQuantity());
            orderDetails.setProductCost(item.getProduct().getCost());
            orderDetails.setShippingCost(shippingRate.getRate());
            orderDetails.setUnitPrice(item.getProduct().getDiscountPrice());
            orderDetails.setSubtotal(item.getProduct().getDiscountPrice() + shippingRate.getRate());
            orderDetails.setProduct(item.getProduct());
            orderDetails.setCurrency(item.getCurrency());
            order.addDetails(orderDetails);
        }

        repository.save(order);
        cartItemService.emptyCartOfCustomer();
        return new JpaResult(JpaResultType.SUCCESSFUL, "Order is placed successfully.", order.getId(), order);
    }

    public JpaResult delete(long id) {
        try {
            repository.deleteById(id);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully deleted order.");
        } catch (Exception ex) {
            log.error("Deleting order, error {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED, "Failed to delete order.");
        }
    }
}
