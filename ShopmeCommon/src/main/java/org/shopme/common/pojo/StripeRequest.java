package org.shopme.common.pojo;

import java.util.List;

public record StripeRequest(
        List<StripeLineItemRequest> items
) { }
