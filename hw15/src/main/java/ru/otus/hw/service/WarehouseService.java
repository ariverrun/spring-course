package ru.otus.hw.service;

import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ru.otus.hw.model.OrderLine;
import ru.otus.hw.model.StockReservation;

@Slf4j
@Service
public class WarehouseService {

    private final Random random = new Random();

    public StockReservation reserve(OrderLine line) {
        log.info("Reserving {} x '{}'", line.quantity(), line.product().name());

        if (random.nextInt(5) == 0) {
            log.warn("Insufficient stock for '{}' (requested: {})", line.product().name(), line.quantity());
            throw new InsufficientStockException(line.product().name(), line.quantity());
        }

        var reservation = new StockReservation(UUID.randomUUID().toString(), line.product(), line.quantity());
        log.info("Reserved {} x '{}', reservationId={}", line.quantity(), line.product().name(), reservation.id());
        return reservation;
    }

    public void release(StockReservation reservation) {
        log.info("Releasing reservation id={}, {} x '{}'",
                reservation.id(), reservation.quantity(), reservation.product().name());
    }
}
