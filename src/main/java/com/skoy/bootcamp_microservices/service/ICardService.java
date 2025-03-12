package com.skoy.bootcamp_microservices.service;

import com.skoy.bootcamp_microservices.dto.CardDTO;
import com.skoy.bootcamp_microservices.model.request.MakePaymentRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ICardService {

    Flux<CardDTO> findAll();
    Mono<CardDTO> findById(String id);
    Mono<CardDTO> create(CardDTO accountDTO);
    Mono<CardDTO> update(String id, CardDTO accountDTO);
    Mono<Void> delete(String id);
    Flux<CardDTO> findAllByBankAccountId(String bankAccountId, LocalDateTime dateFrom, LocalDateTime dateTo);
    Flux<CardDTO> findAllByCustomerId(String customerId);
    Mono<Boolean> makePayment(MakePaymentRequest makePaymentRequest);
    Mono<BigDecimal> getMainAccountBalance(String cardId);
}
