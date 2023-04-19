package ru.practicum.statistic.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statistic.dto.EndpointHitDto;
import ru.practicum.statistic.dto.EndpointHitsResultDto;
import ru.practicum.statistic.service.StatisticService;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@Validated
@RequiredArgsConstructor
@Slf4j
public class StatisticController {

    private final StatisticService statisticService;

    @PostMapping(path = "/hit")
    @ResponseStatus(code = HttpStatus.OK)
    public void saveHit(
            @Validated @RequestBody EndpointHitDto hitDto
    ) {
        log.info("Запрос на сохранение статистики: {}", hitDto);
        statisticService.saveHit(hitDto);
    }

    @GetMapping(path = "/stats")
    public List<EndpointHitsResultDto> getStats(
            @RequestParam("start") LocalDateTime start,
            @RequestParam("end") LocalDateTime end,
            @RequestParam(defaultValue = "") List<String> uris,
            @RequestParam(required = false) boolean unique
    ) {
        log.info("Запрос на получение статистики: start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);
        return statisticService.getStats(start, end, uris, unique);
    }

}
