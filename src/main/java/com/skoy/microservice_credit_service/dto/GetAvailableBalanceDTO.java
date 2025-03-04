package com.skoy.microservice_credit_service.dto;

import com.skoy.microservice_credit_service.enums.CreditTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAvailableBalanceDTO {
    private String customerId;
    private CreditTypeEnum creditType;
}