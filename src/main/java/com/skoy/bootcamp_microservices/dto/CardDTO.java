package com.skoy.bootcamp_microservices.dto;

import com.skoy.bootcamp_microservices.enums.CardStatusEnum;
import com.skoy.bootcamp_microservices.enums.CardTypeEnum;
import com.skoy.bootcamp_microservices.enums.CurrencyEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {
    private String id;
    private String bankAccountId;
    private String mainBankAccountId;
    private CardTypeEnum cardType;
    private String cardHolderName;
    private String cardNumber;
    private LocalDate expirationDate;
    private String cvv;
    private LocalDateTime issueDate;
    private CardStatusEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String customerId;
}
