package ru.practicum.statistic.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statistic.dto.EndpointHitDto;
import ru.practicum.statistic.dto.ViewStatsDto;
import ru.practicum.statistic.service.StatisticService;
import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class StatisticController {

    private final StatisticService statisticService;

    @PostMapping(path = "/hit")
    public void saveHit(
            @Valid @RequestBody EndpointHitDto hitDto
    ) {
        statisticService.saveHit(hitDto);
    }

    @GetMapping(path = "/stats")
    public ViewStatsDto getStats(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam String[] uris,
            @RequestParam boolean unique
    ) {
        return statisticService.getStats(start, end, uris, unique);
    }

}
