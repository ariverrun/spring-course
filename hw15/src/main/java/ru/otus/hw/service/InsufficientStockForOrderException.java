package ru.otus.hw.service;

import ru.otus.hw.model.FailedOrderReservation;

public class InsufficientStockForOrderException extends RuntimeException {

    private final FailedOrderReservation failedOrderReservation;

    public InsufficientStockForOrderException(FailedOrderReservation failedOrderReservation, Throwable cause) {
        super(cause);
        this.failedOrderReservation = failedOrderReservation;
    }

    public FailedOrderReservation getFailedOrderReservation() {
        return failedOrderReservation;
    }
}
