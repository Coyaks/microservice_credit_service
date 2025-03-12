package com.skoy.bootcamp_microservices.dto;

import com.skoy.bootcamp_microservices.enums.AccountTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountDTO {
    private String id;
    private String customerId;
    private AccountTypeEnum accountType;
    private String accountNumber;
    private BigDecimal availableBalance;
    private List<String> owners;
    private List<String> authorizedSigners;
    private BigDecimal maintenanceCommission;

    private int maxFreeTransactions;
    private BigDecimal transactionCommission;
    private int transactionCount;
    private LocalDateTime createdAt;
}
