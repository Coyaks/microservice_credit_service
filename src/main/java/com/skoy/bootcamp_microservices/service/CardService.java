package com.skoy.bootcamp_microservices.service;

import com.skoy.bootcamp_microservices.dto.*;
import com.skoy.bootcamp_microservices.enums.TransactionTypeEnum;
import com.skoy.bootcamp_microservices.mapper.CardMapper;
import com.skoy.bootcamp_microservices.model.Card;
import com.skoy.bootcamp_microservices.model.Transaction;
import com.skoy.bootcamp_microservices.model.request.MakePaymentRequest;
import com.skoy.bootcamp_microservices.repository.ICardRepository;
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
public class CardService implements ICardService {

    private final ICardRepository repository;
    private final WebClient.Builder webClientBuilder;
    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    @Value("${services.customer}")
    private String customerServiceUrl;

    @Value("${services.bankaccount}")
    private String bankAccountServiceUrl;

    @Override
    public Flux<CardDTO> findAll() {
        return repository.findAll()
                .map(CardMapper::toDto);
    }

    @Override
    public Mono<CardDTO> findById(String id) {
        return repository.findById(id)
                .map(CardMapper::toDto);
    }


    @Override
    public Mono<CardDTO> create(CardDTO cardDTO) {
        return createFinal(cardDTO);
    }

    private Mono<CardDTO> createFinal(CardDTO cardDTO) {
        Card card = CardMapper.toEntity(cardDTO);
        card.setCreatedAt(LocalDateTime.now());
        return repository.save(card)
                .map(CardMapper::toDto);
    }

    @Override
    public Mono<CardDTO> update(String id, CardDTO accountDTO) {
        return repository.findById(id)
                .flatMap(item -> {
                    item = CardMapper.toEntity(accountDTO);
                    item.setUpdatedAt(LocalDateTime.now());
                    return repository.save(item);
                })
                .map(CardMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<CardDTO> findAllByBankAccountId(String customerId, LocalDateTime dateFrom, LocalDateTime dateTo) {

        LocalDateTime endOfDay = dateTo != null ? dateTo.withHour(23).withMinute(59).withSecond(59).withNano(999999999) : null;
        return repository.findAllByBankAccountId(customerId)
                .filter(item -> (dateFrom == null || !item.getCreatedAt().isBefore(dateFrom)) &&
                        (endOfDay == null || !item.getCreatedAt().isAfter(endOfDay))).map(CardMapper::toDto);
    }

    @Override
    public Flux<CardDTO> findAllByCustomerId(String customerId) {
        return webClientBuilder.build()
                .get()
                .uri(bankAccountServiceUrl + "/bank_accounts/customer/" + customerId)
                .retrieve()
                .bodyToFlux(BankAccountDTO.class)
                .flatMap(bankAccount -> findAllByBankAccountId(bankAccount.getId(), null, null));
    }

    @Override
    public Mono<Boolean> makePayment(MakePaymentRequest makePaymentRequest) {
        return findById(makePaymentRequest.getCardId())
                .flatMap(card -> {
                    String bankAccountId = makePaymentRequest.isMainBankAccount() ? card.getMainBankAccountId() : card.getBankAccountId();
                    return webClientBuilder.build()
                            .get()
                            .uri(bankAccountServiceUrl + "/bank_accounts/" + bankAccountId)
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<ApiResponse<BankAccountDTO>>() {
                            })
                            .flatMap(response -> {
                                if (response.getStatusCode() == 200 && response.getData() != null) {
                                    BankAccountDTO bankAccount = response.getData();
                                    BigDecimal amount = makePaymentRequest.getAmount();
                                    if (bankAccount.getAvailableBalance().compareTo(amount) >= 0) {
                                        return applyTransaction(bankAccount, amount, makePaymentRequest)
                                                .thenReturn(true);
                                    } else {
                                        return Mono.error(new RuntimeException("Fondos insuficientes"));
                                        //return processFallbackAccounts(card, amount);
                                    }
                                } else {
                                    return Mono.error(new RuntimeException("Error al obtener la cuenta bancaria"));
                                }
                            });
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Card not found")));
    }

    private Mono<Boolean> applyTransaction(BankAccountDTO account, BigDecimal amount, MakePaymentRequest makePaymentRequest) {
        UpdateBalanceDTO dataSend = new UpdateBalanceDTO(account.getId(), TransactionTypeEnum.WITHDRAWAL, amount);
        return webClientBuilder.build()
                .post()
                .uri(bankAccountServiceUrl + "/bank_accounts/update_balance")
                .bodyValue(dataSend)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<BankAccountDTO>>() {
                })
                .flatMap(response -> {
                    if (response.getStatusCode() == 200 && response.getData() != null) {
                        BankAccountDTO updatedAccount = response.getData();
                        // Aquí puedes manejar el objeto updatedAccount si es necesario

                        Transaction transaction = new Transaction();
                        transaction.setCustomerId(account.getCustomerId());
                        transaction.setProductTypeId(account.getId());
                        transaction.setProductType(Transaction.ProductTypeEnum.BANK_ACCOUNT);
                        transaction.setTransactionType(Transaction.TransactionTypeEnum.WITHDRAWAL);
                        transaction.setCardType(Transaction.CardTypeEnum.DEBIT);
                        transaction.setCardId(makePaymentRequest.getCardId());
                        transaction.setAmount(amount);

                        return Transaction.createTransaction(webClientBuilder, transaction)
                                .thenReturn(true);
                        //return Mono.just(true);
                    } else {
                        return Mono.error(new RuntimeException("Error al actualizar el balance"));
                    }
                });
    }


    private Mono<Boolean> processFallbackAccounts_(CardDTO card, BigDecimal amount) {
        return webClientBuilder.build()
                .get()
                .uri(bankAccountServiceUrl + "/bank_accounts/customer/" + card.getCustomerId())
                .retrieve()
                .bodyToFlux(BankAccountDTO.class)
                .map(BankAccountDTO::getId)
                .flatMap(accountId -> webClientBuilder.build()
                        .get()
                        .uri(bankAccountServiceUrl + "/bank_accounts/" + accountId)
                        .retrieve()
                        .bodyToMono(BankAccountDTO.class))
                .filter(account -> account.getAvailableBalance().compareTo(amount) >= 0)
                .next()
                .flatMap(account -> applyTransaction(account, amount, null))
                .switchIfEmpty(Mono.just(false));
    }

    @Override
    public Mono<BigDecimal> getMainAccountBalance(String cardId) {
        return findById(cardId)
                .flatMap(card -> {
                    String mainBankAccountId = card.getMainBankAccountId();
                    return webClientBuilder.build()
                            .get()
                            .uri(bankAccountServiceUrl + "/bank_accounts/" + mainBankAccountId)
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<ApiResponse<BankAccountDTO>>() {
                            })
                            .flatMap(response -> {
                                if (response.getStatusCode() == 200 && response.getData() != null) {
                                    return Mono.just(response.getData().getAvailableBalance());
                                } else {
                                    return Mono.error(new RuntimeException("Error al obtener la cuenta bancaria"));
                                }
                            });
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Card not found")));
    }


}