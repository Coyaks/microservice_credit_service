package com.skoy.bootcamp_microservices.enums;

public enum TransactionTypeEnum {
    DEPOSIT("Deposito"),
    WITHDRAWAL("Retiro"),
    TRANSFER("Transferencia"),
    PURCHASE("Compra"); //cargar consumo

    private final String name;

    TransactionTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}