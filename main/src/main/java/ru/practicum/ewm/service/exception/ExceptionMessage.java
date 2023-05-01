package ru.practicum.ewm.service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ExceptionMessage {

    private final String error;
    private final LocalDateTime localDateTime = LocalDateTime.now();
    private final String path;
    private final String status;

}
