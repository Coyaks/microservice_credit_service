package com.skoy.bootcamp_microservices.repository;

import com.skoy.bootcamp_microservices.model.Credit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ICreditRepository extends ReactiveMongoRepository<Credit, String> {
    Flux<Credit> findAllByCustomerId(String customerId);
    //Flux<BankAccountDTO> findAllAccountByCustomerId(String customerId);
}
