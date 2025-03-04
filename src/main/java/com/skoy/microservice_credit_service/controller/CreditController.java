package com.skoy.microservice_credit_service.controller;

import com.skoy.microservice_credit_service.dto.CreditDTO;
import com.skoy.microservice_credit_service.dto.GetAvailableBalanceDTO;
import com.skoy.microservice_credit_service.dto.UpdateBalanceDTO;
import com.skoy.microservice_credit_service.enums.CreditTypeEnum;
import com.skoy.microservice_credit_service.model.Credit;
import com.skoy.microservice_credit_service.service.ICreditService;
import com.skoy.microservice_credit_service.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;


@RestController
@RequestMapping("/api/v1/credits")
public class CreditController {

    private static final Logger logger = LoggerFactory.getLogger(CreditController.class);

    @Autowired
    private ICreditService service;

    @GetMapping
    public Flux<CreditDTO> findAll() {
        logger.info("Fetching all credits");

        return service.findAll()
                .doOnNext(item -> logger.info("credits found: {}", item))
                .doOnComplete(() -> logger.info("All credits fetched successfully."));
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<CreditDTO>> findById(@PathVariable String id) {
        logger.info("Fetching credits with ID: {}", id);
        return service.findById(id)
                .map(customer -> {
                    logger.info("credits found: {}", customer);
                    return new ApiResponse<>("Cliente encontrado", customer, 200);
                })
                .switchIfEmpty(Mono.just(new ApiResponse<>("Cliente no encontrado", null, 404)))
                .doOnError(e -> logger.error("Error fetching credits with ID: {}", id, e));
    }


    @PostMapping
    public Mono<ApiResponse<CreditDTO>> create(@RequestBody CreditDTO creditDto) {
        logger.info("Creating new credits: {}", creditDto);
        return service.create(creditDto)
                .map(createdItem -> {
                    if (createdItem != null) {
                        logger.info("credits created successfully: {}", createdItem);
                        return new ApiResponse<>("ok", createdItem, 200);
                    } else {
                        logger.error("Error creating credits");
                        return new ApiResponse<>("error", null, 500);
                    }
                });
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<CreditDTO>> update(@PathVariable String id, @RequestBody CreditDTO creditDto) {
        logger.info("Updating credits with ID: {}", id);
        return service.findById(id)
                .flatMap(existingItem -> service.update(id, creditDto)
                        .map(updatedItem -> {
                            logger.info("credits updated successfully: {}", updatedItem);
                            return new ApiResponse<>("Actualizado correctamente", updatedItem, 200);
                        }))
                .switchIfEmpty(Mono.just(new ApiResponse<>("ID no encontrado", null, 404)))
                .doOnError(e -> logger.error("Error updating credits with ID: {}", id, e));
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> delete(@PathVariable String id) {
        logger.info("Deleting credits with ID: {}", id);
        return service.findById(id)
                .flatMap(existingItem -> service.delete(id)
                        .then(Mono.just(new ApiResponse<Void>("Eliminado correctamente", null, 200))))
                .switchIfEmpty(Mono.just(new ApiResponse<Void>("ID no encontrado", null, 404)))
                .onErrorResume(e -> {
                    logger.error("Error deleting credits with ID: {}", id, e);
                    return Mono.just(new ApiResponse<Void>("Error al eliminar", null, 500));
                });
    }

    @GetMapping("/types")
    public CreditTypeEnum[] getAllCreditTypes() {
        return CreditTypeEnum.values();
    }

    @GetMapping("/customer/{customerId}")
    public Flux<CreditDTO> findAllByCustomerId(@PathVariable String customerId) {
        logger.info("Fetching credits for customer ID: {}", customerId);
        return service.findAllByCustomerId(customerId)
                .doOnNext(item -> logger.info("credits found: {}", item))
                .doOnComplete(() -> logger.info("All credits for customer fetched successfully."));
    }

    @PostMapping("/update_balance")
    public Mono<ApiResponse<Credit>> updateBalance(@RequestBody UpdateBalanceDTO updateBalanceDTO) {
        logger.info("Updating balance for bank account ID: {}", updateBalanceDTO.getProductTypeId());
        return service.updateBalance(updateBalanceDTO)
                .map(updatedAccount -> new ApiResponse<>("Balance actualizado correctamente", updatedAccount, 200))
                .doOnError(e -> logger.error("Error updating balance for bank account ID: {}", updateBalanceDTO.getProductTypeId(), e));
    }

    @PostMapping("/charge_consumption")
    public Mono<ApiResponse<Credit>> chargeConsumptionCreditCard(@RequestBody UpdateBalanceDTO updateBalanceDTO) {
        return service.chargeConsumptionCreditCard(updateBalanceDTO)
                .map(updatedAccount -> new ApiResponse<>("chargeConsumption actualizado correctamente", updatedAccount, 200))
                .doOnError(e -> logger.error("Error updating chargeConsumption for bank account ID: {}", updateBalanceDTO.getProductTypeId(), e));
    }

    @PostMapping("/check_available_balance")
    public Mono<ApiResponse<BigDecimal>> getAvailableBalanceByCustomerId(@RequestBody GetAvailableBalanceDTO getAvailableBalanceDTO) {
        return service.getAvailableBalanceByCustomerId(getAvailableBalanceDTO)
                .map(balance -> new ApiResponse<>("Saldo disponible encontrado", balance, 200))
                .doOnError(e -> logger.error("Error fetching available balance for customer ID: {} and account type: {}", getAvailableBalanceDTO.getCustomerId(), getAvailableBalanceDTO.getCreditType(), e));
    }


}
