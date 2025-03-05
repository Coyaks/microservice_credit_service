package com.skoy.bootcamp_microservices.mapper;

import com.skoy.bootcamp_microservices.dto.CreditDTO;
import com.skoy.bootcamp_microservices.model.Credit;

public class CreditMapper {

    public static Credit toEntity(CreditDTO dto) {
        Credit item = new Credit();
        item.setId(dto.getId());
        item.setCustomerId(dto.getCustomerId());
        item.setCreditType(dto.getCreditType());
        item.setCreditLimit(dto.getCreditLimit());
        item.setAvailableBalance(dto.getAvailableBalance());
        item.setUsedCredit(dto.getUsedCredit());
        item.setCurrency(dto.getCurrency());
        return item;
    }

    public static CreditDTO toDto(Credit item) {
        CreditDTO dto = new CreditDTO();
        dto.setId(item.getId());
        dto.setCustomerId(item.getCustomerId());
        dto.setCreditType(item.getCreditType());
        dto.setCreditLimit(item.getCreditLimit());
        dto.setAvailableBalance(item.getAvailableBalance());
        dto.setUsedCredit(item.getUsedCredit());
        dto.setCurrency(item.getCurrency());
        return dto;
    }
}
