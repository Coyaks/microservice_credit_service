package com.skoy.bootcamp_microservices.enums;

public enum CreditStatusEnum {
    ACTIVE("Activo"),
    BLOCKED("Bloqueado"),
    EXPIRED("Expirado"),
    DEBT("Deuda"),;

    private final String name;

    CreditStatusEnum(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
