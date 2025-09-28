package org.shopme.common.enumeration;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    COD, STRIPE;

    public static PaymentMethod findByName(String name) {
        for (PaymentMethod value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }

        return null;
    }
}
