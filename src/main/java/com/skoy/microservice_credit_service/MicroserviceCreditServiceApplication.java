package com.skoy.microservice_credit_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication(scanBasePackages = "com.skoy.microservice_credit_service")
public class MicroserviceCreditServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceCreditServiceApplication.class, args);
	}

	@Bean
	public WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}

}
