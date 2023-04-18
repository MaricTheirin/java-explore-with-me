package ru.practicum.statistic.dto;

import lombok.Value;
import javax.validation.constraints.NotBlank;

@Value
public class EndpointHitsResultDto {

    @NotBlank
    String app;

    @NotBlank
    String uri;

    long hits;

}
