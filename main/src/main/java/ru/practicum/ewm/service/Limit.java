package ru.practicum.ewm.service;

public class Limit {

    public static String limitString(String input, int maxLength) {
        return input == null ? "" : input.substring(0, Math.min(input.length(), maxLength)).concat("...");
    }

}
