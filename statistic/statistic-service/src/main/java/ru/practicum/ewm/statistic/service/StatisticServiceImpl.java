package ru.practicum.ewm.statistic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.statistic.dto.EndpointHitDto;
import ru.practicum.ewm.statistic.dto.EndpointHitsResultDto;
import ru.practicum.ewm.statistic.mapper.EndpointHitDtoMapper;
import ru.practicum.ewm.statistic.model.EndpointHit;
import ru.practicum.ewm.statistic.repository.EndpointHitRepository;
import ru.practicum.ewm.statistic.model.EndpointStats;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final EndpointHitRepository endpointHitRepository;

    @Override
    public List<EndpointHitsResultDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<EndpointStats> stats;
        if (uris.isEmpty()) {
            if (unique) {
                stats = endpointHitRepository.findAllByTimeAndUniqueIp(start, end);
            } else {
                stats = endpointHitRepository.findAllByTime(start, end);
            }
        } else if (unique) {
            stats = endpointHitRepository.findAllByTimeAndFilterByUriInAndUniqueIp(start, end, uris);
        } else {
            stats = endpointHitRepository.findAllByTimeAndFilterByUri(start, end, uris);
        }
        log.info("Получен список обращений: {}", stats);
        return stats.stream().map(EndpointHitDtoMapper::mapEndpointStatsToDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void saveHit(EndpointHitDto hitDto) {
        EndpointHit endpointHit = endpointHitRepository.saveAndFlush(EndpointHitDtoMapper.mapDtoToEndpointHit(hitDto));
        log.info("Обращение {} сохранено", endpointHit);
    }

}
