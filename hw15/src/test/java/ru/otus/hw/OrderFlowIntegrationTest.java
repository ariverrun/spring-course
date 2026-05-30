package ru.otus.hw;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import ru.otus.hw.gateway.OrderGateway;
import ru.otus.hw.model.Customer;
import ru.otus.hw.model.OrderLine;
import ru.otus.hw.model.OrderRequest;
import ru.otus.hw.model.Product;
import ru.otus.hw.model.StockReservation;
import ru.otus.hw.service.InsufficientStockException;
import ru.otus.hw.service.NotificationService;
import ru.otus.hw.service.WarehouseService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderFlowIntegrationTest {

    @Autowired
    private OrderGateway gateway;

    @MockitoBean
    private WarehouseService warehouseService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private CommandLineRunner commandLineRunner;

    @Test
    void shouldSendConfirmationWhenAllItemsReserved() {
        var product = new Product("p-1", "Desk");
        var line = new OrderLine(product, 1);
        var request = new OrderRequest("r-1", new Customer("c-1"), List.of(line));
        var reservation = new StockReservation("res-1", product, 1);

        when(warehouseService.reserve(line)).thenReturn(reservation);

        gateway.submit(request);

        verify(notificationService, after(1000)).notifyOrderConfirmed(any());
        verify(notificationService, never()).notifyOrderFailed(any(), any());
    }

    @Test
    void shouldRollbackAndSendFailureWhenStockInsufficient() {
        var product1 = new Product("p-1", "Desk");
        var product2 = new Product("p-2", "Chair");
        var line1 = new OrderLine(product1, 1);
        var line2 = new OrderLine(product2, 2);
        var request = new OrderRequest("r-1", new Customer("c-1"), List.of(line1, line2));
        var reservation1 = new StockReservation("res-1", product1, 1);

        when(warehouseService.reserve(line1)).thenReturn(reservation1);
        when(warehouseService.reserve(line2)).thenThrow(new InsufficientStockException("Chair", 2));

        gateway.submit(request);

        verify(warehouseService, after(1000)).release(reservation1);
        verify(notificationService).notifyOrderFailed("c-1", "r-1");
        verify(notificationService, never()).notifyOrderConfirmed(any());
    }
}
