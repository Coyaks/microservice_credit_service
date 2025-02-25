package com.skoy.microservice_credit_service.controller;

import com.skoy.microservice_credit_service.dto.CreditDTO;
import com.skoy.microservice_credit_service.service.ICreditService;
import com.skoy.microservice_credit_service.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


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
    public Mono<CreditDTO> findById(@PathVariable String id) {
        logger.info("Fetching credits with ID: {}", id);
        return service.findById(id)
                .doOnNext(item -> logger.info("credits found: {}", item))
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


}
