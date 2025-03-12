package com.skoy.bootcamp_microservices.controller;

import com.skoy.bootcamp_microservices.dto.CardDTO;
import com.skoy.bootcamp_microservices.enums.CardStatusEnum;
import com.skoy.bootcamp_microservices.enums.CardTypeEnum;
import com.skoy.bootcamp_microservices.model.request.MakePaymentRequest;
import com.skoy.bootcamp_microservices.service.ICardService;
import com.skoy.bootcamp_microservices.utils.ApiResponse;
import com.skoy.bootcamp_microservices.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;


@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private static final Logger logger = LoggerFactory.getLogger(CardController.class);

    @Autowired
    private ICardService service;

    @GetMapping
    public Flux<CardDTO> findAll() {
        logger.info("Fetching all cards");

        return service.findAll()
                .doOnNext(item -> logger.info("cards found: {}", item))
                .doOnComplete(() -> logger.info("All cards fetched successfully."));
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<CardDTO>> findById(@PathVariable String id) {
        logger.info("Fetching cards with ID: {}", id);
        return service.findById(id)
                .map(customer -> {
                    logger.info("cards found: {}", customer);
                    return new ApiResponse<>("Credito encontrado", customer, Constants.STATUS_OK);
                })
                .switchIfEmpty(Mono.just(new ApiResponse<>("Credito no encontrado", null, Constants.STATUS_E404)))
                .doOnError(e -> logger.error("Error fetching cards with ID: {}", id, e));
    }


    @PostMapping
    public Mono<ApiResponse<CardDTO>> create(@RequestBody CardDTO cardDto) {
        logger.info("Creating new cards: {}", cardDto);
        return service.create(cardDto)
                .map(createdItem -> {
                    if (createdItem != null) {
                        logger.info("cards created successfully: {}", createdItem);
                        return new ApiResponse<>("ok", createdItem, Constants.STATUS_OK);
                    } else {
                        logger.error("Error creating cards");
                        return new ApiResponse<>("error", null, Constants.STATUS_E500);
                    }
                });
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<CardDTO>> update(@PathVariable String id, @RequestBody CardDTO cardDto) {
        logger.info("Updating cards with ID: {}", id);
        return service.findById(id)
                .flatMap(existingItem -> service.update(id, cardDto)
                        .map(updatedItem -> {
                            logger.info("cards updated successfully: {}", updatedItem);
                            return new ApiResponse<>("Actualizado correctamente", updatedItem, Constants.STATUS_OK);
                        }))
                .switchIfEmpty(Mono.just(new ApiResponse<>("ID no encontrado", null, Constants.STATUS_E404)))
                .doOnError(e -> logger.error("Error updating cards with ID: {}", id, e));
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> delete(@PathVariable String id) {
        logger.info("Deleting cards with ID: {}", id);
        return service.findById(id)
                .flatMap(existingItem -> service.delete(id)
                        .then(Mono.just(new ApiResponse<Void>("Eliminado correctamente", null, Constants.STATUS_OK))))
                .switchIfEmpty(Mono.just(new ApiResponse<Void>("ID no encontrado", null, Constants.STATUS_E404)))
                .onErrorResume(e -> {
                    logger.error("Error deleting cards with ID: {}", id, e);
                    return Mono.just(new ApiResponse<Void>("Error al eliminar", null, Constants.STATUS_E500));
                });
    }

    @GetMapping("/types")
    public CardTypeEnum[] getAllCardTypes() {
        return CardTypeEnum.values();
    }

    @GetMapping("/status")
    public CardStatusEnum[] getAllCardStatus() {
        return CardStatusEnum.values();
    }

    @GetMapping("/bank_account/{bankAccountId}")
    public Flux<CardDTO> findAllByBankAccountId(
            @PathVariable String bankAccountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo
    ) {
        logger.info("Fetching all cards by customer ID: {} from: {} to: {}", bankAccountId, dateFrom, dateTo);
        return service.findAllByBankAccountId(
                        bankAccountId,
                dateFrom != null ? dateFrom.atStartOfDay() : null,
                dateTo != null ? dateTo.atStartOfDay() : null
                )
                .doOnNext(item -> logger.info("cards found: {}", item))
                .doOnComplete(() -> logger.info("All cards for customer fetched successfully."));
    }

    @GetMapping("/customer/{customerId}")
    public Flux<CardDTO> findAllByCustomerId(@PathVariable String customerId) {
        logger.info("Fetching all cards by customer ID: {}", customerId);
        return service.findAllByCustomerId(customerId)
                .doOnNext(item -> logger.info("cards found: {}", item))
                .doOnComplete(() -> logger.info("All cards for customer fetched successfully."));
    }


    @PostMapping("/make_payment")
    public Mono<ApiResponse<Boolean>> makePayments(@RequestBody MakePaymentRequest makePaymentRequest) {
        logger.info("Processing transaction for card ID: {} with amount: {}", makePaymentRequest.getCardId(), makePaymentRequest.getAmount());
        return service.makePayment(makePaymentRequest)
                .map(success -> {
                    if (success) {
                        return new ApiResponse<>("Transacción exitosa", true, Constants.STATUS_OK);
                    } else {
                        return new ApiResponse<>("Fondos insuficientes", false, Constants.STATUS_E400);
                    }
                });
    }


    @GetMapping("main_account_balance/{cardId}")
    public Mono<ApiResponse<BigDecimal>> getMainAccountBalance(@PathVariable String cardId) {
        logger.info("Fetching main account balance for card ID: {}", cardId);
        return service.getMainAccountBalance(cardId)
                .map(balance -> new ApiResponse<>("Saldo obtenido exitosamente", balance, Constants.STATUS_OK))
                .onErrorResume(e -> {
                    logger.error("Error fetching main account balance for card ID: {}", cardId, e);
                    return Mono.just(new ApiResponse<>("Error al obtener el saldo", null, Constants.STATUS_E500));
                });
    }

}
