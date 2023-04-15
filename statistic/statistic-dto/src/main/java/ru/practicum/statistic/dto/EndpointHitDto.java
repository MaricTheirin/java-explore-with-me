package ru.practicum.statistic.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.InetAddress;
import java.net.URI;
import java.time.LocalDateTime;

@Value
@Setter
public class EndpointHitDto {

    @NotBlank
    String app;

    @NotBlank
    URI uri;

    @NotNull
    InetAddress ip;

    @NotNull
    LocalDateTime timestamp;

}
