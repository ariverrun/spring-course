package ru.otus.hw.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.model.OrderRequest;

@MessagingGateway
public interface OrderGateway {

    @Gateway(requestChannel = "orderRequestChannel")
    void submit(OrderRequest orderRequest);
}
