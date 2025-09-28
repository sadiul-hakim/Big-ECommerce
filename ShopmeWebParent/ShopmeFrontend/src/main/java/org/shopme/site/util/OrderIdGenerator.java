package org.shopme.site.util;

import java.security.SecureRandom;

public class OrderIdGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generateOrderId() {
        long timestamp = System.currentTimeMillis();
        int randomNum = random.nextInt(9999);
        return "ORD-" + timestamp + "-" + randomNum;
    }
}
