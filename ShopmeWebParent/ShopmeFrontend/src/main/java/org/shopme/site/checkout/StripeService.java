package org.shopme.site.checkout;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.CartItem;
import org.shopme.common.entity.ShippingRate;
import org.shopme.common.enumeration.CheckoutStatus;
import org.shopme.common.pojo.StripeResponse;
import org.shopme.site.cart.CartItemService;
import org.shopme.site.shipping.ShippingRateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${payment.stripe.secret_key:''}")
    private String secretKey;

    private final CartItemService cartItemService;
    private final ShippingRateService shippingRateService;

    public StripeResponse checkout(HttpServletRequest servletRequest) {

        String baseUrl = servletRequest.getScheme() + "://" +
                servletRequest.getServerName() + ":" +
                servletRequest.getServerPort() +
                servletRequest.getContextPath();

        String successUrl = baseUrl + "/order/place/STRIPE";
        String cancelUrl = baseUrl + "/checkout/page"; // todo: show some message may be

        Optional<ShippingRate> shippingRateOptional = shippingRateService.currentCustomerShipping();
        if (shippingRateOptional.isEmpty()) {
            return new StripeResponse(CheckoutStatus.FAILED, "No shipping available", null, null);
        }

        ShippingRate shippingRate = shippingRateOptional.get();

        List<CartItem> items = cartItemService.findAllCartItemOfCustomer();
        Stripe.apiKey = secretKey;

        SessionCreateParams.Builder sessionBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl);


        // Loop over items and add each as a Stripe line item
        for (CartItem item : items) {

            long unitAmount = Math.round(item.getProduct().getDiscountPrice() * 100); // price per unit in cents

            var productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(item.getProduct().getName())
                    .build();

            var priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency(item.getCurrency())
                    .setUnitAmount(unitAmount)  // Stripe expects "per-unit" in smallest currency (e.g. cents)
                    .setProductData(productData)
                    .build();

            var lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity((long) item.getQuantity())
                    .setPriceData(priceData)
                    .build();

            sessionBuilder.addLineItem(lineItem);
        }

        CartItem item = items.getFirst();
        var shippingData = SessionCreateParams.ShippingOption.builder()
                .setShippingRateData(
                        SessionCreateParams.ShippingOption.ShippingRateData.builder()
                                .setDisplayName("Standard Shipping")
                                .setType(SessionCreateParams.ShippingOption.ShippingRateData.Type.FIXED_AMOUNT) // REQUIRED
                                .setFixedAmount(
                                        SessionCreateParams.ShippingOption.ShippingRateData.FixedAmount.builder()
                                                .setAmount((long) Math.round(shippingRate.getRate() * 100))
                                                .setCurrency(item.getCurrency())
                                                .build()
                                )
                                .build()
                )
                .build();
        sessionBuilder.addShippingOption(shippingData);

        Session session = null;
        try {
            session = Session.create(sessionBuilder.build());
        } catch (StripeException e) {
            log.error("Failed to create stripe session!", e);
        }

        if (session == null) {
            return new StripeResponse(
                    CheckoutStatus.FAILED,
                    "Could not create Payment Session.",
                    null,
                    null
            );
        }

        return new StripeResponse(
                CheckoutStatus.IN_PROGRESS,
                "Payment session created",
                session.getId(),
                session.getUrl()
        );
    }
}
