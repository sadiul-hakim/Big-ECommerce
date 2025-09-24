package org.shopme.common.enumeration;

public enum OrderStatus {
    NEW, CANCELED, PROCESSING, PENDING, PACKAGED, PICKED, SHIPPING, DELIVERED, RETURNED, PAID, REFUNDED;


    public static OrderStatus findByName(String name) {
        for (OrderStatus value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }

        return null;
    }
}
