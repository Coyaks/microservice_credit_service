package com.skoy.microservice_credit_service.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "credits")
public class Credit {
    @Id
    private String id;
    private String customerId;
    private String creditType; // PERSONAL, EMPRESARIAL, TARJETA_CREDITO
    private BigDecimal amount;
    private BigDecimal balance;
    private String currency; // PEN, USD
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}