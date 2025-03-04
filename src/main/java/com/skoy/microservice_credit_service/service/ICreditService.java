package com.skoy.microservice_credit_service.service;

import com.skoy.microservice_credit_service.dto.CreditDTO;
import com.skoy.microservice_credit_service.dto.GetAvailableBalanceDTO;
import com.skoy.microservice_credit_service.dto.UpdateBalanceDTO;
import com.skoy.microservice_credit_service.model.Credit;
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
