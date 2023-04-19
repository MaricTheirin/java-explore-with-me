package ru.practicum.statistic.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.statistic.dto.EndpointHitDto;
import ru.practicum.statistic.dto.EndpointHitsResultDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StatisticClient extends BaseClient {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ParameterizedTypeReference<List<EndpointHitsResultDto>> resultType =
            new ParameterizedTypeReference<>() {};

    @Autowired
    public StatisticClient(
            @Value("${statistic-server.url}") String serverUrl,
            RestTemplateBuilder builder
    ) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }


    public void saveHit(EndpointHitDto endpointHitDto) {
        log.info("Запрошена отправка информации на сервер статистики: {}", endpointHitDto);
        post("/hit", endpointHitDto);
    }

    public List<EndpointHitsResultDto> getStats(
            LocalDateTime start,
            LocalDateTime end
    ) {
        return getStats(start, end, Collections.emptyList(), false);
    }

    public List<EndpointHitsResultDto> getStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris
    ) {
        return getStats(start, end, uris, false);
    }

    public List<EndpointHitsResultDto> getStats(
            LocalDateTime start,
            LocalDateTime end,
            boolean unique
    ) {
        return getStats(start, end, Collections.emptyList(), unique);
    }

    public List<EndpointHitsResultDto> getStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            boolean unique
    ) {
        log.info("Запрошена статистика. Период {}-{}; список URL: {}; только уникальные: {}", start, end, uris, unique);
        Map<String, Object> parameters = Map.of(
                "start", start.format(DATE_TIME_FORMATTER),
                "end", end.format(DATE_TIME_FORMATTER),
                "uris", uris.size() == 0 ? "" : uris,
                "unique", unique
        );

        ResponseEntity<List<EndpointHitsResultDto>> response =
                get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", resultType, parameters);

        log.debug("Результат запроса: {}", response);
        return response.getBody();
    }
}


