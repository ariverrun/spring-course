package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

import java.util.concurrent.Executors;
import ru.otus.hw.model.Order;
import ru.otus.hw.model.OrderRequest;
import ru.otus.hw.model.FailedOrderReservation;
import ru.otus.hw.service.NotificationService;
import ru.otus.hw.service.OrderService;

@Slf4j
@Configuration
public class OrderIntegrationFlow {

    @Bean
    MessageChannel orderRequestChannel() {
        return new ExecutorChannel(Executors.newSingleThreadExecutor());
    }

    @Bean
    MessageChannel rollbackChannel() {
        return new DirectChannel();
    }

    @Bean
    IntegrationFlow orderFlow(OrderService orderService, NotificationService notificationService) {
        return IntegrationFlow.from("orderRequestChannel")
                .handle((payload, headers) -> orderService.processRequest((OrderRequest) payload))
                .handle(msg -> notificationService.notifyOrderConfirmed((Order) msg.getPayload()))
                .get();
    }

    @Bean
    IntegrationFlow rollbackFlow(OrderService orderService, NotificationService notificationService) {
        return IntegrationFlow.from("rollbackChannel")
                .handle((payload, headers) -> orderService.rollback((FailedOrderReservation) payload))
                .handle(msg -> {
                    var request = (OrderRequest) msg.getPayload();
                    notificationService.notifyOrderFailed(request.customer().id(), request.id());
                })
                .get();
    }
}
