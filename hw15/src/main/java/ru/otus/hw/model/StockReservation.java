package ru.otus.hw.model;

public record StockReservation(
    String id,
    Product product,
    int quantity
) {
}
