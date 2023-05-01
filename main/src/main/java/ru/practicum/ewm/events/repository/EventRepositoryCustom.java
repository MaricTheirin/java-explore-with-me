package ru.practicum.ewm.events.repository;

import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventSort;
import ru.practicum.ewm.events.model.EventState;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventRepositoryCustom {

    List<Event> getEvents(
            String text,
            Set<Long> categories,
            boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            EventSort sort,
            int from,
            int size
    );

    List<Event> getAdminEvents(
            Set<Long> userIds,
            EventState[] states,
            Set<Long> categoryIds,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    );

}
