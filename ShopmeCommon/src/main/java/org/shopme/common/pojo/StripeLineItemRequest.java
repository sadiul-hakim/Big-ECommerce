package org.shopme.common.pojo;

public record StripeLineItemRequest(
        long amount,     // unit amount in cents
        long quantity,
        String name,
        String currency
) { }