package com.skoy.bootcamp_microservices.service;

import com.skoy.bootcamp_microservices.dto.BankAccountDTO;
import com.skoy.bootcamp_microservices.dto.CardDTO;
import com.skoy.bootcamp_microservices.dto.UpdateBalanceDTO;
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

    private static final int END_OF_DAY_HOUR = 23;
    private static final int END_OF_DAY_MINUTE = 59;
    private static final int END_OF_DAY_SECOND = 59;
    private static final int END_OF_DAY_NANO = 999999999;
    private static final int HTTP_STATUS_OK = 200;


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
        LocalDateTime endOfDay = dateTo != null ? dateTo.withHour(END_OF_DAY_HOUR).withMinute(END_OF_DAY_MINUTE).withSecond(END_OF_DAY_SECOND).withNano(END_OF_DAY_NANO) : null;
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
                                if (response.getStatusCode() == HTTP_STATUS_OK && response.getData() != null) {
                                    BankAccountDTO bankAccount = response.getData();
                                    BigDecimal amount = makePaymentRequest.getAmount();
                                    if (bankAccount.getAvailableBalance().compareTo(amount) >= 0) {
                                        return applyTransaction(bankAccount, amount, makePaymentRequest)
                                                .thenReturn(true);
                                    } else {
                                        return Mono.error(new RuntimeException("Fondos insuficientes"));
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
                    if (response.getStatusCode() == HTTP_STATUS_OK && response.getData() != null) {
                        BankAccountDTO updatedAccount = response.getData();

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
                    } else {
                        return Mono.error(new RuntimeException("Error al actualizar el balance"));
                    }
                });
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
                                if (response.getStatusCode() == HTTP_STATUS_OK && response.getData() != null) {
                                    return Mono.just(response.getData().getAvailableBalance());
                                } else {
                                    return Mono.error(new RuntimeException("Error al obtener la cuenta bancaria"));
                                }
                            });
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Card not found")));
    }


}