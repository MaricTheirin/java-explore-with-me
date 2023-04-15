package ru.practicum.statistic.service;

import ru.practicum.statistic.dto.EndpointHitDto;
import ru.practicum.statistic.dto.ViewStatsDto;
import java.time.LocalDateTime;

public interface StatisticService {

    ViewStatsDto getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique);

    void saveHit(EndpointHitDto hitDto);

}
