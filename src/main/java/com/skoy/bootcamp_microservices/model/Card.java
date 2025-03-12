package com.skoy.bootcamp_microservices.model;

import com.skoy.bootcamp_microservices.enums.CardStatusEnum;
import com.skoy.bootcamp_microservices.enums.CardTypeEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "cards")
public class Card {
    @Id
    private String id;
    private String bankAccountId;
    private String mainBankAccountId;
    private CardTypeEnum cardType;
    private String cardHolderName;
    private String cardNumber;
    private LocalDate expirationDate;
    private String cvv;
    private LocalDateTime issueDate;
    private CardStatusEnum status = CardStatusEnum.ACTIVE;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}