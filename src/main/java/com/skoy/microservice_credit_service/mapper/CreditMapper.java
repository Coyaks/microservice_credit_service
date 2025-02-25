package com.skoy.microservice_credit_service.mapper;

import com.skoy.microservice_credit_service.dto.CreditDTO;
import com.skoy.microservice_credit_service.model.Credit;

public class CreditMapper {

    public static Credit toEntity(CreditDTO dto) {
        Credit item = new Credit();
        item.setId(dto.getId());
        item.setCustomerId(dto.getCustomerId());
        item.setCreditType(dto.getCreditType());
        item.setAmount(dto.getAmount());
        item.setBalance(dto.getBalance());
        item.setCurrency(dto.getCurrency());
        return item;
    }

    public static CreditDTO toDto(Credit item) {
        CreditDTO dto = new CreditDTO();
        dto.setId(item.getId());
        dto.setCustomerId(item.getCustomerId());
        dto.setCreditType(item.getCreditType());
        dto.setAmount(item.getAmount());
        dto.setBalance(item.getBalance());
        dto.setCurrency(item.getCurrency());
        return dto;
    }
}
