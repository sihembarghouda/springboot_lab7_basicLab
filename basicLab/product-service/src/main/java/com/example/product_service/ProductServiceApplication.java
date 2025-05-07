package com.example.product_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// Removed unnecessary import for EnableEurekaClient
@SpringBootApplication
// Removed @EnableEurekaClient as it is no longer needed
public class ProductServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}
}