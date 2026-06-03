package ru.otus.hw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.model.Order;

@Slf4j
@Service
public class NotificationService {

    public void notifyOrderConfirmed(Order order) {
        log.info("Sending order confirmation to customer id={}, orderId={}", order.customer().id(), order.id());
    }

    public void notifyOrderFailed(String customerId, String orderRequestId) {
        log.warn("Sending order failure notification to customer id={}, orderRequestId={}", customerId, orderRequestId);
    }
}
