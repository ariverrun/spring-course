package ru.otus.hw.model;

import java.util.List;

public record OrderRequest(
    String id,
    Customer customer,
    List<OrderLine> lines
) {
}
