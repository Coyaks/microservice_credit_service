package com.skoy.microservice_credit_service.model;

import com.skoy.microservice_credit_service.enums.CardTypeEnum;
import com.skoy.microservice_credit_service.enums.CreditStatusEnum;
import com.skoy.microservice_credit_service.enums.CreditTypeEnum;
import com.skoy.microservice_credit_service.enums.CurrencyEnum;
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
    private CreditTypeEnum creditType;

    private BigDecimal creditLimit;
    private BigDecimal availableBalance;
    private BigDecimal usedCredit = BigDecimal.ZERO;; // Saldo ya utilizado

    private CurrencyEnum currency;
    private CreditStatusEnum status;
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