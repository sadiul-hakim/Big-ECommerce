package org.shopme.site.cart;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.Address;
import org.shopme.common.entity.CartItem;
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
        double totalPrice = items.stream().map(CartItem::getTotalPrice).reduce(0.0, Double::sum);
        Address address = addressService.currentCustomerActiveAddress();

        if (address == null) {
            model.addAttribute(NO_ACTIVE_ADDRESS, true);
            model.addAttribute(MESSAGE, "Please select and address.");
        }

        boolean shippingAvailable = shippingRateService.shippingAvailable();
        model.addAttribute("shippingAvailable", shippingAvailable);
        model.addAttribute("activeAddress", address);
        model.addAttribute("cartItems", items);
        model.addAttribute("totalPrices", numberFormatter.format(totalPrice));
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
    @GetMapping("/increment-quantity")
    public ResponseEntity<?> incrementQuantity(@RequestParam int cartItemId, HttpServletRequest request) {
        NumberFormatter numberFormater = NumberFormatter.getFormatter(request);
        var res = cartItemService.incrementQuantity(cartItemId, numberFormater);
        return ResponseEntity.ok(res);
    }

    @ResponseBody
    @GetMapping("/decrement-quantity")
    public ResponseEntity<?> decrementQuantity(@RequestParam int cartItemId, HttpServletRequest request) {
        NumberFormatter numberFormater = NumberFormatter.getFormatter(request);
        var res = cartItemService.decrementQuantity(cartItemId, numberFormater);
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
