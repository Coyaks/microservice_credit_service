package com.skoy.microservice_credit_service.service;

import com.skoy.microservice_credit_service.dto.CreditDTO;
import com.skoy.microservice_credit_service.dto.CustomerDTO;
import com.skoy.microservice_credit_service.dto.GetAvailableBalanceDTO;
import com.skoy.microservice_credit_service.dto.UpdateBalanceDTO;
import com.skoy.microservice_credit_service.enums.TransactionTypeEnum;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditService implements ICreditService {

    private final ICreditRepository repository;
    private final WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(CreditService.class);

    @Value("${customer.service.url}")
    private String customerServiceUrl;

    @Value("${bankaccount.service.url}")
    private String bankAccountServiceUrl;

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
                .flatMap(rspCustomer -> {
                    CustomerDTO customer = rspCustomer.getData();
                    if (customer == null) return Mono.error(new RuntimeException("Cliente no encontrado"));
                    // Un cliente puede tener un producto de crédito sin la obligación de tener una cuenta bancaria
                    logger.info("customer {}: {}", customerServiceUrl, customer);

                    return repository.save(CreditMapper.toEntity(accountDTO))
                            .map(CreditMapper::toDto);
                })
                .doOnError(error -> logger.error("Error al validar cliente: {}", error.getMessage()));
    }

    @Override
    public Mono<CreditDTO> update(String id, CreditDTO accountDTO) {
        return repository.findById(id)
                .flatMap(item -> {
                    item = CreditMapper.toEntity(accountDTO);
                    item.setUpdatedAt(LocalDateTime.now());
                    return repository.save(item);
                })
                .map(CreditMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<CreditDTO> findAllByCustomerId(String customerId) {
        return repository.findAllByCustomerId(customerId)
                .map(CreditMapper::toDto);
    }


    @Override
    public Mono<Credit> updateBalance(UpdateBalanceDTO updateBalanceDTO) {
        if (updateBalanceDTO.getProductTypeId() == null || updateBalanceDTO.getProductTypeId().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID de la cuenta bancaria no puede estar vacío"));
        }

        return repository.findById(updateBalanceDTO.getProductTypeId())
                .flatMap(account -> {
                    BigDecimal newBalance;
                    if (updateBalanceDTO.getTransactionType() == TransactionTypeEnum.DEPOSIT) {
                        newBalance = account.getAvailableBalance().add(updateBalanceDTO.getAmount());
                    } else if (updateBalanceDTO.getTransactionType() == TransactionTypeEnum.WITHDRAWAL) {
                        newBalance = account.getAvailableBalance().subtract(updateBalanceDTO.getAmount());
                    } else {
                        return Mono.error(new IllegalArgumentException("Tipo de transacción no válido"));
                    }
                    account.setAvailableBalance(newBalance);
                    return repository.save(account);
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Cuenta bancaria no encontrada")));
    }

    @Override
    public Mono<Credit> chargeConsumptionCreditCard(UpdateBalanceDTO updateBalanceDTO) {
        return repository.findById(updateBalanceDTO.getProductTypeId())
                .flatMap(card -> {
                    BigDecimal availableCredit = card.getCreditLimit().subtract(card.getUsedCredit());
                    if (updateBalanceDTO.getAmount().compareTo(availableCredit) > 0) {
                        return Mono.error(new RuntimeException("Crédito insuficiente"));
                    }
                    card.setUsedCredit(card.getUsedCredit().add(updateBalanceDTO.getAmount()));
                    card.setAvailableBalance(card.getAvailableBalance().subtract(updateBalanceDTO.getAmount()));
                    return repository.save(card);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("productTypeId no encontrado")));
    }

    @Override
    public Mono<BigDecimal> getAvailableBalanceByCustomerId(GetAvailableBalanceDTO getAvailableBalanceDTO) {
        return webClientBuilder.build()
                .get()
                .uri(customerServiceUrl + "/customers/" + getAvailableBalanceDTO.getCustomerId())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CustomerDTO>>() {})
                .flatMap(response -> {
                    if (response.getData() == null) return Mono.error(new RuntimeException("Cliente no encontrado"));
                    return repository.findAllByCustomerId(getAvailableBalanceDTO.getCustomerId())
                            .filter(account -> account.getCreditType().equals(getAvailableBalanceDTO.getCreditType()))
                            .singleOrEmpty()
                            .map(Credit::getAvailableBalance)
                            .switchIfEmpty(Mono.error(new RuntimeException("No se encontró una cuenta con el tipo especificado para el cliente")));
                });
    }


}