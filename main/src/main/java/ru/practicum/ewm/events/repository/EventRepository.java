package ru.practicum.ewm.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.events.model.Event;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends
        JpaRepository<Event, Long>, EventRepositoryCustom, QuerydslPredicateExecutor<Event> {

    boolean existsByCategoryId(long categoryId);

    boolean existsByInitiator_IdAndId(long userId, long eventId);

    List<Event> findAllByInitiator_Id(long userId, Pageable pageable);

    Optional<Event> findByInitiator_IdAndId(long userId, long eventId);

    Set<Event> findAllByIdIn(Collection<Long> ids);

}
