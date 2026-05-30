package ru.otus.hw.service;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String productName, int quantity) {
        super("Insufficient stock for '" + productName + "' (requested: " + quantity + ")");
    }
}
