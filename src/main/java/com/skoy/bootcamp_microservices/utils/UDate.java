package com.skoy.bootcamp_microservices.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UDate {

    public static String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}
