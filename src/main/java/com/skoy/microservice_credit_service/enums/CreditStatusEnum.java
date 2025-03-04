package com.skoy.microservice_credit_service.enums;

public enum CreditStatusEnum {
    ACTIVE("Activo"),
    BLOCKED("Bloqueado"),
    EXPIRED("Expirado"),;

    private final String name;

    CreditStatusEnum(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
