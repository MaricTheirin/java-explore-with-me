package ru.practicum.ewm.events.service;

import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.EventResponseDto;
import ru.practicum.ewm.events.dto.EventShortResponseDto;
import ru.practicum.ewm.events.model.EventSort;
import ru.practicum.ewm.events.model.EventState;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {

    List<EventResponseDto> adminGetEvents(
            Set<Long> userIds,
            EventState[] states,
            Set<Long> categoryIds,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    );

    EventResponseDto adminUpdateEvent(long eventId, EventDto eventDto);

    EventResponseDto userUpdateEvent(long userId, long eventId, EventDto eventDto);

    List<EventResponseDto> getEvents(
            String text,
            Set<Long> categories,
            boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            EventSort sort,
            int from,
            int size,
            String ip,
            String url
    );

    EventResponseDto getEvent(long id, String ip, String url);

    List<EventShortResponseDto> getEvents(long userId, int from, int size);

    EventResponseDto getEvent(long userId, long eventId);

    EventResponseDto createEvent(long userId, EventDto eventDto);

}
