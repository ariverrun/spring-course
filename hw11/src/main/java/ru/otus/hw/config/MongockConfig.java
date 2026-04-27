package ru.otus.hw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.reactivestreams.client.MongoClient;

import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver;
import io.mongock.runner.springboot.EnableMongock;

@Configuration
@EnableMongock
public class MongockConfig {
    
    @Value("${spring.data.mongodb.database}")
    private String databaseName;
    
    @Bean
    public ConnectionDriver connectionDriver(MongoClient mongoClient) {
        return MongoReactiveDriver.withDefaultLock(mongoClient, databaseName);
    }
}