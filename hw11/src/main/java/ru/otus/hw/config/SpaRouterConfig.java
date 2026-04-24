package ru.otus.hw.config;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SpaRouterConfig {
    
    @Bean
    public RouterFunction<ServerResponse> spaRouter() {
        return RouterFunctions.route(
            GET("/"),
            request -> ServerResponse.ok().bodyValue(new ClassPathResource("static/index.html"))
        ).andRoute(
            GET("/books/**"),
            request -> ServerResponse.ok().bodyValue(new ClassPathResource("static/index.html"))
        ).andRoute(
            GET("/authors/**"),
            request -> ServerResponse.ok().bodyValue(new ClassPathResource("static/index.html"))
        ).andRoute(
            GET("/genres/**"),
            request -> ServerResponse.ok().bodyValue(new ClassPathResource("static/index.html"))
        );
    }
}