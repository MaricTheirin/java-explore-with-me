package ru.practicum.ewm.events.service;

import ru.practicum.ewm.events.dto.EventCreateDto;
import ru.practicum.ewm.events.dto.EventResponseDto;
import ru.practicum.ewm.events.dto.EventShortResponseDto;
import ru.practicum.ewm.events.dto.EventUpdateDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventSort;
import ru.practicum.ewm.events.model.EventState;
import java.time.LocalDateTime;
import java.util.*;

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

    EventResponseDto adminUpdateEvent(long eventId, EventUpdateDto eventDto);

    EventResponseDto userUpdateEvent(long userId, long eventId, EventUpdateDto eventDto);

    List<EventResponseDto> getEvents(
            String text,
            Set<Long> categories,
            Boolean paid,
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

    EventResponseDto createEvent(long userId, EventCreateDto eventDto);

    List<EventShortResponseDto> getStatsAndMapToShortResponseDtos(Collection<Event> events);

}