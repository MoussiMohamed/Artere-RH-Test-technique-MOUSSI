package com.artere.cacheservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Cache Service application.
 * <p>
 * This class is responsible for bootstrapping the Spring Boot application.
 */
@SpringBootApplication
public class CacheServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheServiceApplication.class, args);
    }

}
