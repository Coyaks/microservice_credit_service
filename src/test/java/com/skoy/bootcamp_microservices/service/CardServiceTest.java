package com.skoy.bootcamp_microservices.service;

import com.skoy.bootcamp_microservices.dto.BankAccountDTO;
import com.skoy.bootcamp_microservices.dto.CardDTO;
import com.skoy.bootcamp_microservices.mapper.CardMapper;
import com.skoy.bootcamp_microservices.model.Card;
import com.skoy.bootcamp_microservices.model.request.MakePaymentRequest;
import com.skoy.bootcamp_microservices.repository.ICardRepository;
import com.skoy.bootcamp_microservices.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CardServiceTest {

    @Mock
    private ICardRepository cardRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private CardService cardService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        Card card = new Card();
        card.setId("1");
        when(cardRepository.findAll()).thenReturn(Flux.just(card));

        Flux<CardDTO> result = cardService.findAll();

        StepVerifier.create(result)
                .expectNext(CardMapper.toDto(card))
                .verifyComplete();
    }

    @Test
    public void testFindById() {
        Card card = new Card();
        card.setId("1");
        when(cardRepository.findById(anyString())).thenReturn(Mono.just(card));

        Mono<CardDTO> result = cardService.findById("1");

        StepVerifier.create(result)
                .expectNext(CardMapper.toDto(card))
                .verifyComplete();
    }

    @Test
    public void testCreate() {
        Card card = new Card();
        card.setId("1");
        when(cardRepository.save(any(Card.class))).thenReturn(Mono.just(card));

        Mono<CardDTO> result = cardService.create(CardMapper.toDto(card));

        StepVerifier.create(result)
                .expectNext(CardMapper.toDto(card))
                .verifyComplete();
    }

    @Test
    public void testUpdate() {
        Card card = new Card();
        card.setId("1");
        when(cardRepository.findById(anyString())).thenReturn(Mono.just(card));
        when(cardRepository.save(any(Card.class))).thenReturn(Mono.just(card));

        Mono<CardDTO> result = cardService.update("1", CardMapper.toDto(card));

        StepVerifier.create(result)
                .expectNext(CardMapper.toDto(card))
                .verifyComplete();
    }

    @Test
    public void testDelete() {
        when(cardRepository.deleteById(anyString())).thenReturn(Mono.empty());

        Mono<Void> result = cardService.delete("1");

        StepVerifier.create(result)
                .verifyComplete();
    }

    /*@Test
    public void testMakePayment() {
        // Mock data
        MakePaymentRequest request = new MakePaymentRequest("cardId", BigDecimal.TEN, false);
        CardDTO cardDTO = new CardDTO();
        cardDTO.setId("cardId");
        cardDTO.setBankAccountId("bankAccountId");

        BankAccountDTO bankAccountDTO = new BankAccountDTO();
        bankAccountDTO.setId("bankAccountId");
        bankAccountDTO.setAvailableBalance(BigDecimal.valueOf(100));

        ApiResponse<BankAccountDTO> apiResponse = new ApiResponse<>("Success", bankAccountDTO, 200);

        // Mock WebClient
        WebClient webClient = WebClient.builder().build();
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = webClient.get();
        WebClient.RequestHeadersSpec requestHeadersSpec = webClient.get();
        WebClient.ResponseSpec responseSpec = webClient.get().retrieve();

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(apiResponse));

        // Mock repository
        when(cardRepository.findById(anyString())).thenReturn(Mono.just(CardMapper.toEntity(cardDTO)));

        // Test makePayment
        Mono<Boolean> result = cardService.makePayment(request);

        // Verify results
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void testGetMainAccountBalance() {
        // Mock data
        Card card = new Card();
        card.setId("cardId");
        card.setMainBankAccountId("mainBankAccountId");

        BankAccountDTO bankAccountDTO = new BankAccountDTO();
        bankAccountDTO.setId("mainBankAccountId");
        bankAccountDTO.setAvailableBalance(BigDecimal.valueOf(100));

        ApiResponse<BankAccountDTO> apiResponse = new ApiResponse<>("Success", bankAccountDTO, 200);

        // Mock WebClient
        WebClient webClient = WebClient.builder().build();
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = webClient.get();
        WebClient.RequestHeadersSpec requestHeadersSpec = webClient.get();
        WebClient.ResponseSpec responseSpec = webClient.get().retrieve();

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(apiResponse));

        // Mock repository
        when(cardRepository.findById(anyString())).thenReturn(Mono.just(card));

        // Test getMainAccountBalance
        Mono<BigDecimal> result = cardService.getMainAccountBalance("cardId");

        // Verify results
        StepVerifier.create(result)
                .expectNext(BigDecimal.valueOf(100))
                .verifyComplete();
    }*/

}