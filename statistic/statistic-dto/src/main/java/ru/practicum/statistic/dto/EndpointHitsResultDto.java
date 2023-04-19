package ru.practicum.statistic.dto;

import lombok.*;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EndpointHitsResultDto {

    @NotBlank
    String app;

    @NotBlank
    String uri;

    long hits;

}
