package org.shopme.common.util;

public record JpaResult(
        JpaResultType type,
        String message,
        int entityId,
        Object entity
) {

    public JpaResult(JpaResultType type, String message, Object entity) {
        this(type, message, 0, entity);
    }

    public JpaResult(JpaResultType type, String message) {
        this(type, message, 0, null);
    }

    public JpaResult(JpaResultType type, String message, int entityId) {
        this(type, message, entityId, null);
    }
}
