package ru.practicum.statistic.dto;

import lombok.Value;
import javax.validation.constraints.NotBlank;
import java.net.URI;

@Value
public class ViewStatsDto {

    @NotBlank
    String app;

    @NotBlank
    URI uri;

    long hits;

}
