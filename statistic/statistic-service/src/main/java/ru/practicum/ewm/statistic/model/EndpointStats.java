package ru.practicum.ewm.statistic.model;

import lombok.Value;

@Value
public class EndpointStats {
    String app;
    String uri;
    long hits;
}
