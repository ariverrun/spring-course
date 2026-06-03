package ru.otus.hw.model;

import java.util.List;

public record Order(
    String id,
    String orderRequestId,
    Customer customer,
    List<StockReservation> reservations
) {
}
