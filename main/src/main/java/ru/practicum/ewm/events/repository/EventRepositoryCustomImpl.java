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
            Boolean paid,
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
            int from,
            int size
    ) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QEvent event = QEvent.event;
        BooleanExpression filters;

        if (rangeStart == null && rangeEnd == null) {
            filters = event.eventDate.after(LocalDateTime.now());
        } else if (rangeStart == null) {
            filters = event.eventDate.before(rangeEnd);
        } else if (rangeEnd == null) {
            filters = event.eventDate.after(rangeStart);
        } else {
            filters = event.eventDate.between(rangeStart, rangeEnd);
        }

        if (text != null && !text.isBlank()) {
            filters = filters.and(event.description.likeIgnoreCase(text).or(event.annotation.likeIgnoreCase(text)));
        }

        if (userIds.size() > 0) {
            filters = filters.and(event.initiator.id.in(userIds));
        }

        if (categoryIds != null && categoryIds.size() > 0) {
            filters = filters.and(event.category.id.in(categoryIds));
        }

        if (states != null) {
            filters = filters.and(event.state.in(states));
        }

        if (paid != null) {
            filters = filters.and(event.paid.eq(paid));
        }

        List<Event> events = queryFactory
                .selectFrom(event)
                .where(filters)
                .offset(from)
                .limit(size)
                .fetch();

        log.debug("Получен результат: {}", events);

        return events;
    }

}
