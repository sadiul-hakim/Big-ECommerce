package org.shopme.site.checkout;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.Address;
import org.shopme.common.entity.CartItem;
import org.shopme.common.entity.ShippingRate;
import org.shopme.common.enumeration.PaymentMethod;
import org.shopme.common.pojo.StripeResponse;
import org.shopme.common.util.NumberFormatter;
import org.shopme.site.address.AddressService;
import org.shopme.site.cart.CartItemService;
import org.shopme.site.shipping.ShippingRateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private static final String PAGE = "checkout";

    private final CartItemService cartItemService;
    private final AddressService addressService;
    private final ShippingRateService shippingRateService;
    private final StripeService stripeService;

    @GetMapping("/page")
    public String checkoutPage(Model model, HttpServletRequest request) {

        NumberFormatter numberFormatter = NumberFormatter.getFormatter(request);
        List<CartItem> items = cartItemService.findAllCartItemOfCustomer();
        double totalItemsPrice = cartItemService.getTotalItemsPriceOfCustomer(items);
        Address address = addressService.currentCustomerActiveAddress();
        double paymentTotal = cartItemService.getPaymentTotal(items);

        Optional<ShippingRate> shippingRateOptional = shippingRateService.currentCustomerShipping();
        shippingRateOptional.ifPresent(shippingRate -> model.addAttribute("shipping", shippingRate));

        model.addAttribute("paymentTotal", numberFormatter.format(paymentTotal));
        model.addAttribute("shippingAvailable", shippingRateOptional.isPresent());
        model.addAttribute("address", address);
        model.addAttribute("cartItems", items);
        model.addAttribute("totalItemsPrice", numberFormatter.format(totalItemsPrice));

        return PAGE;
    }

    @GetMapping("/{method}")
    public String checkout(@PathVariable PaymentMethod method, HttpServletRequest request) {

        // If th method is COD place the order.
        // Take the information from logged-in users cart and selected address.
        if (method.equals(PaymentMethod.COD)) {
            return "redirect:/order/place/" + method.name();
        }

        if (method.equals(PaymentMethod.STRIPE)) {
            StripeResponse response = stripeService.checkout(request);
            return "redirect:" + response.url(); // send user to Stripe checkout
        }

        return "redirect:/checkout/page";
    }
}
