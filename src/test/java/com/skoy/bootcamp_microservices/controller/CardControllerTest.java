package com.skoy.bootcamp_microservices.controller;

import com.skoy.bootcamp_microservices.dto.CardDTO;
import com.skoy.bootcamp_microservices.model.request.MakePaymentRequest;
import com.skoy.bootcamp_microservices.service.ICardService;
import com.skoy.bootcamp_microservices.utils.ApiResponse;
import com.skoy.bootcamp_microservices.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(CardController.class)
public class CardControllerTest {

    @MockBean
    private ICardService cardService;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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
        CardDTO cardDTO = new CardDTO();
        cardDTO.setId("1");
        when(cardService.findById(anyString())).thenReturn(Mono.just(cardDTO));

        webTestClient.get().uri("/api/v1/cards/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiResponse<CardDTO>>() {})
                .consumeWith(response -> {
                    ApiResponse<CardDTO> apiResponse = response.getResponseBody();
                    assert apiResponse != null;
                    assertEquals("Credito encontrado", apiResponse.getMessage());
                    assertEquals(cardDTO.getId(), apiResponse.getData().getId());
                    assertEquals(Constants.STATUS_OK, apiResponse.getStatusCode());
                });
    }


    @Test
    public void testCreate() {
        CardDTO cardDTO = new CardDTO();
        when(cardService.create(any(CardDTO.class))).thenReturn(Mono.just(cardDTO));

        webTestClient.post().uri("/api/v1/cards")
                .bodyValue(cardDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiResponse<CardDTO>>() {})
                .consumeWith(response -> {
                    ApiResponse<CardDTO> apiResponse = response.getResponseBody();
                    assert apiResponse != null;
                    assertEquals("ok", apiResponse.getMessage());
                    assertEquals(cardDTO, apiResponse.getData());
                    assertEquals(Constants.STATUS_OK, apiResponse.getStatusCode());
                });
    }


    @Test
    public void testUpdate() {
        CardDTO cardDTO = new CardDTO();
        cardDTO.setId("1");
        when(cardService.findById(anyString())).thenReturn(Mono.just(cardDTO));
        when(cardService.update(anyString(), any(CardDTO.class))).thenReturn(Mono.just(cardDTO));

        webTestClient.put().uri("/api/v1/cards/1")
                .bodyValue(cardDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiResponse<CardDTO>>() {})
                .consumeWith(response -> {
                    ApiResponse<CardDTO> apiResponse = response.getResponseBody();
                    assert apiResponse != null;
                    assertEquals("Actualizado correctamente", apiResponse.getMessage());
                    assertEquals(cardDTO.getId(), apiResponse.getData().getId());
                    assertEquals(Constants.STATUS_OK, apiResponse.getStatusCode());
                });
    }


    @Test
    public void testDelete() {
        when(cardService.findById(anyString())).thenReturn(Mono.just(new CardDTO()));
        when(cardService.delete(anyString())).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/cards/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiResponse<Void>>() {})
                .consumeWith(response -> {
                    ApiResponse<Void> apiResponse = response.getResponseBody();
                    assert apiResponse != null;
                    assertEquals("Eliminado correctamente", apiResponse.getMessage());
                    assertEquals(Constants.STATUS_OK, apiResponse.getStatusCode());
                });
    }


    /*@Test
    public void testFindAllByBankAccountId() {
        when(cardService.findAllByBankAccountId(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Flux.just(new CardDTO()));

        webTestClient.get().uri("/api/v1/cards/bank_account/1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CardDTO.class);
    }*/

    @Test
    public void testFindAllByCustomerId() {
        when(cardService.findAllByCustomerId(anyString())).thenReturn(Flux.just(new CardDTO()));

        webTestClient.get().uri("/api/v1/cards/customer/1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CardDTO.class);
    }

    /*@Test
    public void testMakePayments() {
        MakePaymentRequest request = new MakePaymentRequest("cardId", BigDecimal.TEN, false);
        when(cardService.makePayment(any(MakePaymentRequest.class))).thenReturn(Mono.just(true));

        webTestClient.post().uri("/api/v1/cards/make_payment")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class)
                .isEqualTo(new ApiResponse<>("Transacción exitosa", true, Constants.STATUS_OK));
    }

    @Test
    public void testGetMainAccountBalance() {
        when(cardService.getMainAccountBalance(anyString())).thenReturn(Mono.just(BigDecimal.TEN));

        webTestClient.get().uri("/api/v1/cards/main_account_balance/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class)
                .isEqualTo(new ApiResponse<>("Saldo obtenido exitosamente", BigDecimal.TEN, Constants.STATUS_OK));
    }*/

}