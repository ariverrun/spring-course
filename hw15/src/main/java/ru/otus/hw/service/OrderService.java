package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.model.FailedOrderReservation;
import ru.otus.hw.model.Order;
import ru.otus.hw.model.OrderRequest;
import ru.otus.hw.model.StockReservation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final WarehouseService warehouseService;

    public Order processRequest(OrderRequest request) {
        log.info("Processing order request id={}, customer={}", request.id(), request.customer().id());

        List<StockReservation> reservations = new ArrayList<>();
        try {
            for (var line : request.lines()) {
                reservations.add(warehouseService.reserve(line));
            }
        } catch (InsufficientStockException e) {
            log.warn("Reservation failed: {}. Rolling back {} reservation(s)", e.getMessage(), reservations.size());
            throw new InsufficientStockForOrderException(new FailedOrderReservation(request, reservations), e);
        }

        var order = new Order(UUID.randomUUID().toString(), request.id(), request.customer(), reservations);
        log.info("Order created id={}, reservations={}", order.id(), reservations.size());
        return order;
    }

    public OrderRequest rollback(FailedOrderReservation failed) {
        log.warn("Rolling back order request id={}", failed.orderRequest().id());
        failed.reservations().forEach(warehouseService::release);
        log.warn("Rollback complete for order request id={}", failed.orderRequest().id());
        return failed.orderRequest();
    }
}
