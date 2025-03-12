package com.skoy.bootcamp_microservices.controller;

import com.skoy.bootcamp_microservices.dto.CardDTO;
import com.skoy.bootcamp_microservices.enums.CardStatusEnum;
import com.skoy.bootcamp_microservices.enums.CardTypeEnum;
import com.skoy.bootcamp_microservices.model.request.MakePaymentRequest;
import com.skoy.bootcamp_microservices.service.ICardService;
import com.skoy.bootcamp_microservices.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CardControllerTestBK {

    @Mock
    private ICardService cardService;

    @InjectMocks
    private CardController cardController;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(cardController).build();
    }

    @Test
    public void testFindAll() {
        when(cardService.findAll()).thenReturn(Flux.just(new CardDTO()));

        webTestClient.get().uri("/api/v1/cards")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CardDTO.class);
    }

    @Test
    public void testFindById() {
        when(cardService.findById(anyString())).thenReturn(Mono.just(new CardDTO()));

        webTestClient.get().uri("/api/v1/cards/{id}", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class);
    }

    @Test
    public void testCreate() {
        when(cardService.create(any(CardDTO.class))).thenReturn(Mono.just(new CardDTO()));

        webTestClient.post().uri("/api/v1/cards")
                .bodyValue(new CardDTO())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class);
    }

    @Test
    public void testUpdate() {
        when(cardService.findById(anyString())).thenReturn(Mono.just(new CardDTO()));
        when(cardService.update(anyString(), any(CardDTO.class))).thenReturn(Mono.just(new CardDTO()));

        webTestClient.put().uri("/api/v1/cards/{id}", "1")
                .bodyValue(new CardDTO())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class);
    }

    @Test
    public void testDelete() {
        when(cardService.findById(anyString())).thenReturn(Mono.just(new CardDTO()));
        when(cardService.delete(anyString())).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/cards/{id}", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class);
    }

    @Test
    public void testGetAllCardTypes() {
        webTestClient.get().uri("/api/v1/cards/types")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CardTypeEnum[].class);
    }

    @Test
    public void testGetAllCardStatus() {
        webTestClient.get().uri("/api/v1/cards/status")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CardStatusEnum[].class);
    }

    @Test
    public void testFindAllByBankAccountId() {
        when(cardService.findAllByBankAccountId(anyString(), any(), any())).thenReturn(Flux.just(new CardDTO()));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/api/v1/cards/bank_account/{bankAccountId}")
                        .queryParam("dateFrom", LocalDate.now().toString())
                        .queryParam("dateTo", LocalDate.now().toString())
                        .build("1"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CardDTO.class);
    }

    @Test
    public void testFindAllByCustomerId() {
        when(cardService.findAllByCustomerId(anyString())).thenReturn(Flux.just(new CardDTO()));

        webTestClient.get().uri("/api/v1/cards/customer/{customerId}", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CardDTO.class);
    }

    @Test
    public void testMakePayments() {
        when(cardService.makePayment(any(MakePaymentRequest.class))).thenReturn(Mono.just(true));

        webTestClient.post().uri("/api/v1/cards/make_payment")
                .bodyValue(new MakePaymentRequest())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class);
    }

    @Test
    public void testGetMainAccountBalance() {
        when(cardService.getMainAccountBalance(anyString())).thenReturn(Mono.just(BigDecimal.TEN));

        webTestClient.get().uri("/api/v1/cards/main_account_balance/{cardId}", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class);
    }
}