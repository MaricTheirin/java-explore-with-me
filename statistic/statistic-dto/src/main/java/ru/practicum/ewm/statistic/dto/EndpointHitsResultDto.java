package ru.practicum.ewm.statistic.dto;

import lombok.*;

@Value
public class EndpointHitsResultDto {

    String app;

    String uri;

    long hits;

}
