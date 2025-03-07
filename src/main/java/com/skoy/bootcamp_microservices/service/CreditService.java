package com.skoy.bootcamp_microservices.service;

import com.skoy.bootcamp_microservices.dto.CreditDTO;
import com.skoy.bootcamp_microservices.dto.CustomerDTO;
import com.skoy.bootcamp_microservices.dto.GetAvailableBalanceDTO;
import com.skoy.bootcamp_microservices.dto.UpdateBalanceDTO;
import com.skoy.bootcamp_microservices.enums.CreditStatusEnum;
import com.skoy.bootcamp_microservices.enums.CreditTypeEnum;
import com.skoy.bootcamp_microservices.enums.TransactionTypeEnum;
import com.skoy.bootcamp_microservices.mapper.CreditMapper;
import com.skoy.bootcamp_microservices.model.Credit;
import com.skoy.bootcamp_microservices.repository.ICreditRepository;
import com.skoy.bootcamp_microservices.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreditService implements ICreditService {

    private final ICreditRepository repository;
    private final WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(CreditService.class);

    @Value("${services.customer}")
    private String customerServiceUrl;

    @Value("${services.bankaccount}")
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
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CustomerDTO>>() {
                })
                .flatMap(rspCustomer -> {
                    CustomerDTO customer = rspCustomer.getData();
                    if (customer == null) return Mono.error(new RuntimeException("Cliente no encontrado"));
                    // Un cliente puede tener un producto de crédito sin la obligación de tener una cuenta bancaria
                    logger.info("customer {}: {}", customerServiceUrl, customer);

                    // Verificar si el cliente tiene alguna deuda vencida
                    return repository.findAllByCustomerId(accountDTO.getCustomerId())
                            .filter(credit -> credit.getStatus() == CreditStatusEnum.DEBT)
                            .hasElements()
                            .flatMap(hasDebt -> {
                                if (hasDebt) {
                                    return Mono.error(new RuntimeException("El cliente tiene una deuda vencida y no puede adquirir un nuevo producto de crédito"));
                                } else {
                                    if (accountDTO.getCreditType() == CreditTypeEnum.PERSONAL) {
                                        return repository.findAllByCustomerId(accountDTO.getCustomerId())
                                                .filter(credit -> credit.getCreditType() == CreditTypeEnum.PERSONAL)
                                                .hasElements()
                                                .flatMap(hasPersonalCredit -> {
                                                    if (hasPersonalCredit) {
                                                        return Mono.error(new RuntimeException("El cliente ya tiene un crédito personal"));
                                                    } else {
                                                        return repository.save(CreditMapper.toEntity(accountDTO))
                                                                .map(CreditMapper::toDto);
                                                    }
                                                });
                                    } else {
                                        return repository.save(CreditMapper.toEntity(accountDTO))
                                                .map(CreditMapper::toDto);
                                    }
                                }
                            });
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
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CustomerDTO>>() {
                })
                .flatMap(response -> {
                    if (response.getData() == null) return Mono.error(new RuntimeException("Cliente no encontrado"));
                    return repository.findAllByCustomerId(getAvailableBalanceDTO.getCustomerId())
                            .filter(account -> account.getCreditType().equals(getAvailableBalanceDTO.getCreditType()))
                            .singleOrEmpty()
                            .map(Credit::getAvailableBalance)
                            .switchIfEmpty(Mono.error(new RuntimeException("No se encontró una cuenta con el tipo especificado para el cliente")));
                });
    }

    @Override
    public Flux<CreditDTO> getOverdueCredits(String customerId) {
        return repository.findAllByCustomerId(customerId)
                .filter(credit -> credit.getStatus() == CreditStatusEnum.DEBT)
                .map(CreditMapper::toDto);
    }

}