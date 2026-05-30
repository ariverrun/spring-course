package ru.otus.hw;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.otus.hw.gateway.OrderGateway;
import ru.otus.hw.model.Customer;
import ru.otus.hw.model.OrderLine;
import ru.otus.hw.model.OrderRequest;
import ru.otus.hw.model.Product;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner runner(OrderGateway gateway) {
        return args -> {
            var desk = new Product(UUID.randomUUID().toString(), "Office Desk");
            var chair = new Product(UUID.randomUUID().toString(), "Ergonomic Chair");
            var shelf = new Product(UUID.randomUUID().toString(), "Book Shelf");

            var customer = new Customer(UUID.randomUUID().toString());
            var request = new OrderRequest(UUID.randomUUID().toString(), customer, List.of(
                new OrderLine(desk, 1),
                new OrderLine(chair, 4),
                new OrderLine(shelf, 2)
            ));
            gateway.submit(request);
        };
    }
}
