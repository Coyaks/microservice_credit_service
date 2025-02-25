package com.skoy.microservice_credit_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDTO {
    private String id;
    private String customerId;
    private String creditType;
    private BigDecimal amount;
    private BigDecimal balance;
    private String currency;

}
