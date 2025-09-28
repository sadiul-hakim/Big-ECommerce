package org.shopme.common.pojo;

import org.shopme.common.enumeration.CheckoutStatus;

public record StripeResponse(
        CheckoutStatus status,
        String message,
        String sessionId,
        String url
) {
}
