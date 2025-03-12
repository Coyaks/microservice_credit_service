package com.skoy.bootcamp_microservices.dto;

import com.skoy.bootcamp_microservices.enums.TransactionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBalanceDTO {
    private String productTypeId; // bankAccountId
    private TransactionTypeEnum transactionType;
    private BigDecimal amount;
}