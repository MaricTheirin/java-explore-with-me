package ru.practicum.ewm.participations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.participations.model.Participation;
import ru.practicum.ewm.participations.model.ParticipationState;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    List<Participation> getParticipationsByRequester_Id(long userId);

    Optional<Participation> getParticipationByIdAndRequester_Id(long participationId, long userId);

    List<Participation> getParticipationsByEvent_Id(long eventId);

    boolean existsByRequester_IdAndEvent_Id(long userId, long eventId);

    Set<Participation> findByIdInAndStatus(Collection<Long> participationIds, ParticipationState state);

    Set<Participation> findByIdAndStatusIn(long participationId, Collection<ParticipationState> states);

    Set<Participation> findByEvent_IdAndStatusIn(long eventId, Collection<ParticipationState> states);

    @Modifying
    @Query("UPDATE Participation p SET p.status = :status WHERE p.id IN :ids")
    void updateStatusByIdIn(ParticipationState status, Collection<Long> ids);

}
