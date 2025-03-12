package com.skoy.bootcamp_microservices.enums;

public enum CardTypeEnum {
    DEBIT("Debito"),
    CREDIT("Credito");

    private final String name;

    CardTypeEnum(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
