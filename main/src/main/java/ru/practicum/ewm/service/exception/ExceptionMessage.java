package ru.practicum.ewm.service.exception;

import lombok.Value;
import java.time.LocalDateTime;

@Value
public class ExceptionMessage {
    String error;
    LocalDateTime localDateTime = LocalDateTime.now();
    String path;
    String status;
}
