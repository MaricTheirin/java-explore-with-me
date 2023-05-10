package ru.practicum.ewm.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.requests.model.ConfirmedRequestCount;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.model.RequestState;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> getRequestsByRequester_Id(long userId);

    Optional<Request> getRequestByIdAndRequester_Id(long requestId, long userId);

    List<Request> getRequestsByEvent_Id(long eventId);

    boolean existsByRequester_IdAndEvent_Id(long userId, long eventId);

    Set<Request> findByIdInAndStatus(Collection<Long> requestIds, RequestState state);

    Set<Request> findByEvent_IdAndStatusIn(long eventId, Collection<RequestState> states);

    @Modifying
    @Query("UPDATE Request r SET r.status = :status WHERE r.id IN :ids")
    void updateStatusByIdIn(RequestState status, Collection<Long> ids);

    @Query("SELECT new ru.practicum.ewm.requests.model.ConfirmedRequestCount(r.event.id, COUNT(r.id)) " +
            "FROM Request AS r " +
            "WHERE r.event.id IN :eventIds " +
                "AND r.status = :state " +
            "GROUP BY r.event.id " +
            "ORDER BY r.event.id ASC"
    )
    Set<ConfirmedRequestCount> countAllByEvent_IdInAndStatusIs(Collection<Long> eventIds, RequestState state);

    int countByEvent_IdAndStatus(long id, RequestState status);

}
