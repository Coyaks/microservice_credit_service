package com.skoy.microservice_credit_service.dto;

import com.skoy.microservice_credit_service.enums.CreditTypeEnum;
import com.skoy.microservice_credit_service.enums.CurrencyEnum;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDTO {
    private String id;
    private String customerId;
    private CreditTypeEnum creditType;
    private BigDecimal creditLimit;
    private BigDecimal availableBalance;
    private BigDecimal usedCredit;
    private CurrencyEnum currency;

}
