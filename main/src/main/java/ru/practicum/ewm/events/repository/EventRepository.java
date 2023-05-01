package ru.practicum.ewm.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.events.model.Event;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends
        JpaRepository<Event, Long>, EventRepositoryCustom, QuerydslPredicateExecutor<Event> {

    boolean existsByCategoryId(long categoryId);

    List<Event> findAllByInitiator_Id(long userId);

    Optional<Event> findByInitiator_IdAndId(long userId, long eventId);

}
