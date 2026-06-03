package ru.otus.hw.model;

import java.util.List;

public record FailedOrderReservation(
    OrderRequest orderRequest,
    List<StockReservation> reservations
) {
}
