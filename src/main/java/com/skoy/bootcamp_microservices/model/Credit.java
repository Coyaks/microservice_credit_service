package com.skoy.bootcamp_microservices.model;

import com.skoy.bootcamp_microservices.enums.CardTypeEnum;
import com.skoy.bootcamp_microservices.enums.CreditStatusEnum;
import com.skoy.bootcamp_microservices.enums.CreditTypeEnum;
import com.skoy.bootcamp_microservices.enums.CurrencyEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "credits")
public class Credit {
    @Id
    private String id;
    private String customerId;
    private CreditTypeEnum creditType;

    private BigDecimal creditLimit;
    private BigDecimal availableBalance;
    private BigDecimal usedCredit = BigDecimal.ZERO;; // Saldo ya utilizado

    private CurrencyEnum currency;
    //private CreditStatusEnum status;
    private CreditStatusEnum status = CreditStatusEnum.ACTIVE;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //Add new
    private String cardHolderName;
    private LocalDateTime expirationDate;
    private String cvv;
    private String pin;
    private LocalDateTime issueDate;
    private BigDecimal interestRate;
    private CardTypeEnum cardType;
    //Add new

}