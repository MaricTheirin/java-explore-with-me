package ru.practicum.statistic.exception;

import java.time.LocalDateTime;

public class ExceptionMessage {

    private final String error;
    private final LocalDateTime localDateTime;
    private final String path;

    public ExceptionMessage(String message, String path) {
        this.error = message;
        this.localDateTime = LocalDateTime.now();
        this.path = path;
    }

    public String getError() {
        return error;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public String getPath() {
        return path;
    }


}
