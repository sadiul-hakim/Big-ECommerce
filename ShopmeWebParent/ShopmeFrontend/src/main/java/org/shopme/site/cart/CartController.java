package org.shopme.site.cart;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.Address;
import org.shopme.common.entity.CartItem;
import org.shopme.common.entity.ShippingRate;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.NumberFormatter;
import org.shopme.site.address.AddressService;
import org.shopme.site.shipping.ShippingRateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private static final String NO_ACTIVE_ADDRESS = "noActiveAddress";
    private static final String MESSAGE = "message";

    private final CartItemService cartItemService;
    private final AddressService addressService;
    private final ShippingRateService shippingRateService;

    @GetMapping
    public String page(Model model, HttpServletRequest request) {

        NumberFormatter numberFormatter = NumberFormatter.getFormatter(request);
        List<CartItem> items = cartItemService.findAllCartItemOfCustomer();
        double totalItemsPrice = cartItemService.getTotalItemsPriceOfCustomer(items);
        Address address = addressService.currentCustomerActiveAddress();
        double paymentTotal = cartItemService.getPaymentTotal(items);

        if (address == null) {
            model.addAttribute(NO_ACTIVE_ADDRESS, true);
            model.addAttribute(MESSAGE, "Please select and address.");
        }

        Optional<ShippingRate> shippingRateOptional = shippingRateService.currentCustomerShipping();
        shippingRateOptional.ifPresent(shippingRate -> model.addAttribute("shipping", shippingRate));

        model.addAttribute("paymentTotal", numberFormatter.format(paymentTotal));
        model.addAttribute("shippingAvailable", shippingRateOptional.isPresent());
        model.addAttribute("activeAddress", address);
        model.addAttribute("cartItems", items);
        model.addAttribute("totalItemsPrice", numberFormatter.format(totalItemsPrice));
        return "cart_page";
    }

    @ResponseBody
    @GetMapping(value = "/add-to-cart", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addToCart(@RequestParam int productId) {
        JpaResult jpaResult = cartItemService.addToCart(productId);
        return jpaResult.type().equals(JpaResultType.SUCCESSFUL) ?
                ResponseEntity.ok(Map.of("message", jpaResult.message())) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", jpaResult.message()));

    }

    @ResponseBody
    @GetMapping("/update-quantity")
    public ResponseEntity<?> updateQuantity(@RequestParam int cartItemId, @RequestParam int quantity,
                                            @RequestParam boolean replace,
                                            HttpServletRequest request) {
        NumberFormatter numberFormater = NumberFormatter.getFormatter(request);
        var res = cartItemService.updateQuantity(cartItemId, quantity, replace, numberFormater);
        return ResponseEntity.ok(res);
    }

    @ResponseBody
    @GetMapping("/delete-item")
    public ResponseEntity<?> deleteItem(@RequestParam int cartItemId) {
        boolean deleted = cartItemService.deleteCartItem(cartItemId);
        return deleted ? ResponseEntity.ok(Map.of("message", "Successfully deleted item")) :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Failed to remove item from cart"));
    }
}
