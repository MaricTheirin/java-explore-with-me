package ru.practicum.statistic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.statistic.model.EndpointHit;
import ru.practicum.statistic.model.EndpointStats;
import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value =
            "SELECT new ru.practicum.statistic.model.EndpointStats(eh.app, eh.uri, count(eh.app)) " +
            "FROM EndpointHit AS eh " +
            "WHERE eh.timestamp BETWEEN :start AND :end " +
            "GROUP BY eh.uri, eh.app " +
            "ORDER BY 3 DESC"
    )
    List<EndpointStats> findAllByTime(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value =
            "SELECT new ru.practicum.statistic.model.EndpointStats(eh.app, eh.uri, count(distinct eh.ip)) " +
            "FROM EndpointHit AS eh " +
            "WHERE eh.timestamp BETWEEN :start AND :end " +
            "GROUP BY eh.uri, eh.app " +
            "ORDER BY 3 DESC"
    )
    List<EndpointStats> findAllByTimeAndUniqueIp(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value =
            "SELECT new ru.practicum.statistic.model.EndpointStats(eh.app, eh.uri, count(eh.app)) " +
            "FROM EndpointHit AS eh " +
            "WHERE eh.timestamp BETWEEN :start AND :end AND eh.uri IN :uri " +
            "GROUP BY eh.uri, eh.app " +
            "ORDER BY 3 DESC"
    )
    List<EndpointStats> findAllByTimeAndFilterByUri(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uri") List<String> uri
    );

    @Query(value =
            "SELECT new ru.practicum.statistic.model.EndpointStats(eh.app, eh.uri, count(distinct eh.ip)) " +
            "FROM EndpointHit AS eh " +
            "WHERE eh.timestamp BETWEEN :start AND :end AND eh.uri IN (:uri)" +
            "GROUP BY eh.uri, eh.app " +
            "ORDER BY 3 DESC"
    )
    List<EndpointStats> findAllByTimeAndFilterByUriInAndUniqueIp(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uri") List<String> uri
    );

}
