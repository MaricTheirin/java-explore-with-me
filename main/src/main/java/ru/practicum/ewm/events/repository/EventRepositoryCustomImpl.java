package ru.practicum.ewm.events.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventSort;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.model.QEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static ru.practicum.ewm.events.model.EventSort.EVENT_DATE;


@Repository
@Slf4j
@RequiredArgsConstructor
public class EventRepositoryCustomImpl implements EventRepositoryCustom {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<Event> getEvents(
            String text,
            Set<Long> categories,
            boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            EventSort sort,
            int from,
            int size
    ) {
        return getFilteredEvents(
                text,
                Collections.emptySet(),
                categories,
                new EventState[] {EventState.PUBLISHED},
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size
        );
    }

    @Override
    public List<Event> getAdminEvents(
            Set<Long> userIds,
            EventState[] states,
            Set<Long> categoryIds,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    ) {
        return getFilteredEvents(
                "",
                userIds,
                categoryIds,
                states,
                null,
                rangeStart,
                rangeEnd,
                false,
                EVENT_DATE,
                from,
                size
        );
    }

    private List<Event> getFilteredEvents(
            String text,
            Set<Long> userIds,
            Set<Long> categoryIds,
            EventState[] states,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            EventSort sort,
            int from,
            int size
    ) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QEvent event = QEvent.event;

        BooleanExpression filters = event.eventDate.between(rangeStart, rangeEnd);

        if (text != null && !text.isBlank()) {
            filters.and(event.description.likeIgnoreCase(text).or(event.annotation.likeIgnoreCase(text)));
        }

        if (userIds.size() > 0) {
            filters.and(event.initiator.id.in(userIds));
        }

        if (categoryIds != null && categoryIds.size() > 0) {
            filters.and(event.category.id.in(categoryIds));
        }

        if (states != null) {
            filters.and(event.state.in(states));
        }

        if (paid != null) {
            filters.and(event.paid.eq(paid));
        }

        if (onlyAvailable) {
            filters.and(event.participantLimit.gt(event.confirmedRequests));
        }

        List<Event> events = queryFactory
                .selectFrom(event)
                .where(filters)
                .orderBy(sort == EVENT_DATE ? event.eventDate.asc() : event.views.asc())
                .offset(from)
                .limit(size)
                .fetch();

        log.debug("Получен результат: {}", events);

        return events;
    }

}
