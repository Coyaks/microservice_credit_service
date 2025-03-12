package com.skoy.bootcamp_microservices.enums;

public enum CardStatusEnum {
    ACTIVE("Activo"),
    BLOCKED("Bloqueado"),
    EXPIRED("Expirado"),
    DEBT("Deuda"),;

    private final String name;

    CardStatusEnum(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
