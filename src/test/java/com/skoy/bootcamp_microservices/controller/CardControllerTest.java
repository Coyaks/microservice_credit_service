package com.skoy.bootcamp_microservices.controller;

import com.skoy.bootcamp_microservices.dto.CardDTO;
import com.skoy.bootcamp_microservices.service.ICardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

@WebFluxTest(CardController.class)
public class CardControllerTest {

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
}