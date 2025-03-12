package com.skoy.bootcamp_microservices.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MakePaymentRequest {
    private String cardId;
    private BigDecimal amount;
    private boolean isMainBankAccount = false;
}
