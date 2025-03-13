package com.skoy.bootcamp_microservices.repository;

import com.skoy.bootcamp_microservices.model.Card;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ICardRepository extends ReactiveMongoRepository<Card, String> {
    Flux<Card> findAllByBankAccountId(String bankAccountId);
}
