package ru.practicum.ewm.statistic.client.exception;

import org.springframework.http.HttpStatus;

public class ClientException extends RuntimeException {

    public ClientException(HttpStatus status, String message, String body) {
        super(
            String.format(
                "При выполнении запроса произошла ошибка [%s]. Результат запроса: %s",
                status,
                body
            )
        );
    }
}
