package ru.practicum.statistic.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statistic.dto.EndpointHitDto;

import javax.validation.Valid;

@RestController
@Validated
public class StatisticController {

    @PostMapping(path = "/hit")
    public void saveHit(
            @Valid @RequestBody EndpointHitDto hitDto
    ) {

    }

}
