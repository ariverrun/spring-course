package ru.otus.hw.model;

public record OrderLine(
    Product product,
    int quantity
) {
}
