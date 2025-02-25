package com.skoy.microservice_credit_service.service;

import com.skoy.microservice_credit_service.dto.CreditDTO;
import com.skoy.microservice_credit_service.dto.CustomerDTO;
import com.skoy.microservice_credit_service.mapper.CreditMapper;
import com.skoy.microservice_credit_service.model.Credit;
import com.skoy.microservice_credit_service.repository.ICreditRepository;
import com.skoy.microservice_credit_service.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreditService implements ICreditService {

    private final ICreditRepository repository;
    private final WebClient.Builder webClientBuilder;
    private static final Logger log = LoggerFactory.getLogger(CreditService.class);

    @Value("${customer.service.url}")
    private String customerServiceUrl;

    @Override
    public Flux<CreditDTO> findAll() {
        return repository.findAll()
                .map(CreditMapper::toDto);
    }

    @Override
    public Mono<CreditDTO> findById(String id) {
        return repository.findById(id)
                .map(CreditMapper::toDto);
    }

    @Override
    public Mono<CreditDTO> create(CreditDTO accountDTO) {

        return webClientBuilder.build()
                .get()
                .uri(customerServiceUrl + "/customers/" + accountDTO.getCustomerId())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CustomerDTO>>() {})
                .flatMap(response -> {
                    CustomerDTO customer = response.getData();
                    if (customer != null) {
                        return repository.save(CreditMapper.toEntity(accountDTO))
                                .map(CreditMapper::toDto);
                    } else {
                        return Mono.error(new RuntimeException("Cliente no encontrado"));
                    }
                })
                .doOnError(error -> log.error("Error al validar cliente: {}", error.getMessage()));
    }

    @Override
    public Mono<CreditDTO> update(String id, CreditDTO accountDTO) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setCreditType(accountDTO.getCreditType());
                    existing.setAmount(accountDTO.getAmount());
                    existing.setBalance(accountDTO.getBalance());
                    existing.setCurrency(accountDTO.getCurrency());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return repository.save(existing);
                })
                .map(CreditMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

}