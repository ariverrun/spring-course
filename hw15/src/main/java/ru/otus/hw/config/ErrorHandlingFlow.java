package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import ru.otus.hw.model.FailedOrderReservation;
import ru.otus.hw.service.InsufficientStockForOrderException;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ErrorHandlingFlow {

    private final MessageChannel rollbackChannel;

    @Bean
    IntegrationFlow errorFlow() {
        return IntegrationFlow.from("errorChannel")
                .handle((payload, headers) -> {
                    Throwable cause = ((MessagingException) payload).getCause();
                    if (cause instanceof InsufficientStockForOrderException ex) {
                        FailedOrderReservation failed = ex.getFailedOrderReservation();
                        log.warn("Routing to rollback channel for order request id={}", failed.orderRequest().id());
                        rollbackChannel.send(MessageBuilder.withPayload(failed).build());
                    } else {
                        log.error("Unhandled error in order flow", cause);
                    }
                    return null;
                })
                .get();
    }
}
