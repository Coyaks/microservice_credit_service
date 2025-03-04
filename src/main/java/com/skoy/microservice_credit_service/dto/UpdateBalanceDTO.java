package com.skoy.microservice_credit_service.dto;

import com.skoy.microservice_credit_service.enums.TransactionTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateBalanceDTO {
    private String productTypeId; // creditId
    private TransactionTypeEnum transactionType;
    private BigDecimal amount;
}