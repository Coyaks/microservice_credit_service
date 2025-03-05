package com.skoy.bootcamp_microservices.service;

import com.skoy.bootcamp_microservices.dto.CreditDTO;
import com.skoy.bootcamp_microservices.dto.GetAvailableBalanceDTO;
import com.skoy.bootcamp_microservices.dto.UpdateBalanceDTO;
import com.skoy.bootcamp_microservices.model.Credit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ICreditService {

    Flux<CreditDTO> findAll();
    Mono<CreditDTO> findById(String id);
    Mono<CreditDTO> create(CreditDTO accountDTO);
    Mono<CreditDTO> update(String id, CreditDTO accountDTO);
    Mono<Void> delete(String id);
    Flux<CreditDTO> findAllByCustomerId(String customerId);

    Mono<Credit> updateBalance(UpdateBalanceDTO updateBalanceDTO);
    Mono<Credit> chargeConsumptionCreditCard(UpdateBalanceDTO updateBalanceDTO);
    Mono<BigDecimal> getAvailableBalanceByCustomerId(GetAvailableBalanceDTO getAvailableBalanceDTO);
}
