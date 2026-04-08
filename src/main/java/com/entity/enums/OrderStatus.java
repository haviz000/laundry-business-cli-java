package com.entity.enums;

public enum OrderStatus {
    RECEIVED, WASHING, DRYING, IRONING, DONE, PICKED_UP, CANCELLED;

    public OrderStatus next() {
        OrderStatus[] values = OrderStatus.values();
        if (this.ordinal() < values.length - 2) {
            return values[this.ordinal() + 1];
        }
        return this;
    }
}
