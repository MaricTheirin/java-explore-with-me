package ru.practicum.ewm.events.dto;

import lombok.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventLocationDto {

    @Min(value = -90)
    @Max(value = 90)
    private float lat;

    @Min(value = -180)
    @Max(value = 180)
    private float lon;

}
