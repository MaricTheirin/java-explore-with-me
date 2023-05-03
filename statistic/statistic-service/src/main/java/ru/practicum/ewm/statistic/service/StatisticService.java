package ru.practicum.ewm.statistic.service;

import ru.practicum.ewm.statistic.dto.EndpointHitDto;
import ru.practicum.ewm.statistic.dto.EndpointHitsResultDto;
import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {

    List<EndpointHitsResultDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    void saveHit(EndpointHitDto hitDto);

}
