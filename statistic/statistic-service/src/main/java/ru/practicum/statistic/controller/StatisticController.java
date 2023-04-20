package ru.practicum.statistic.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statistic.dto.EndpointHitDto;
import ru.practicum.statistic.dto.EndpointHitsResultDto;
import ru.practicum.statistic.service.StatisticService;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class StatisticController {

    private final StatisticService statisticService;

    @PostMapping(path = "/hit")
    public void saveHit(
            @Valid @RequestBody EndpointHitDto hitDto
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
        checkPeriod(start, end);
        return statisticService.getStats(start, end, uris, unique);
    }

    private void checkPeriod(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Дата начала не может быть позже даты окончания");
        }
    }

}
