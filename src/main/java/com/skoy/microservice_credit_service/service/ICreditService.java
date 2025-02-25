package com.skoy.microservice_credit_service.service;

import com.skoy.microservice_credit_service.dto.CreditDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICreditService {

    Flux<CreditDTO> findAll();
    Mono<CreditDTO> findById(String id);
    Mono<CreditDTO> create(CreditDTO accountDTO);
    Mono<CreditDTO> update(String id, CreditDTO accountDTO);
    Mono<Void> delete(String id);
}
