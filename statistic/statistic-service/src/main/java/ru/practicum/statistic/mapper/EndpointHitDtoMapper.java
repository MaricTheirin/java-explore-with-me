package ru.practicum.statistic.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.statistic.dto.EndpointHitDto;
import ru.practicum.statistic.dto.EndpointHitsResultDto;
import ru.practicum.statistic.model.EndpointHit;
import ru.practicum.statistic.model.EndpointStats;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointHitDtoMapper {

    private static final String OBJECT_MAPPED_MESSAGE = "Выполнено преобразование объекта из {} в {}";

    public static EndpointHit mapDtoToEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit mappedEndpointHit = new EndpointHit(
                0L,
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                endpointHitDto.getTimestamp()
        );
        log.trace(OBJECT_MAPPED_MESSAGE, endpointHitDto, mappedEndpointHit);
        return mappedEndpointHit;
    }

    public static EndpointHitsResultDto mapEndpointStatsToDto(EndpointStats endpointStats) {
        EndpointHitsResultDto mappedDto = new EndpointHitsResultDto(
                endpointStats.getApp(),
                endpointStats.getUri(),
                endpointStats.getHits()
        );
        log.trace(OBJECT_MAPPED_MESSAGE, endpointStats, mappedDto);
        return mappedDto;
    }

}
