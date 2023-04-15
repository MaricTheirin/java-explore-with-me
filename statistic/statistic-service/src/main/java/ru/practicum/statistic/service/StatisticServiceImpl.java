package ru.practicum.statistic.service;

import ru.practicum.statistic.dto.EndpointHitDto;
import ru.practicum.statistic.dto.ViewStatsDto;
import java.time.LocalDateTime;

public class StatisticServiceImpl implements StatisticService {

    @Override
    public ViewStatsDto getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        return null;
    }

    @Override
    public void saveHit(EndpointHitDto hitDto) {

    }

}
