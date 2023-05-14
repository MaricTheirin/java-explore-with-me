package ru.practicum.ewm.events.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.events.dto.EventCreateDto;
import ru.practicum.ewm.events.dto.EventResponseDto;
import ru.practicum.ewm.events.dto.EventShortResponseDto;
import ru.practicum.ewm.events.dto.EventUpdateDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventLocation;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.service.mapper.Mapper;
import ru.practicum.ewm.users.model.User;

import static ru.practicum.ewm.categories.mapper.CategoryDtoMapper.mapCategoryToResponseDto;
import static ru.practicum.ewm.events.mapper.EventLocationMapper.mapEventLocationToResponseDto;
import static ru.practicum.ewm.users.mapper.UserDtoMapper.mapUserToResponseDto;
import static ru.practicum.ewm.users.mapper.UserDtoMapper.mapUserToShortResponseDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class EventDtoMapper extends Mapper {

    public static Event mapDtoToEvent(EventCreateDto eventDto, User initiator, Category category, EventLocation location) {
        Event mappedEvent = Event.builder()
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .category(category)
                .description(eventDto.getDescription())
                .eventDate(eventDto.getEventDate())
                .location(location)
                .initiator(initiator)
                .paid(eventDto.isPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.isRequestModeration())
                .state(EventState.PENDING)
                .build();

        log.trace(DEFAULT_MESSAGE, eventDto, mappedEvent);
        return mappedEvent;
    }

    public static Event mapDtoToEvent(EventUpdateDto eventDto, User initiator, Category category, EventLocation location) {
        Event mappedEvent = Event.builder()
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .category(category)
                .description(eventDto.getDescription())
                .eventDate(eventDto.getEventDate())
                .location(location)
                .initiator(initiator)
                .paid(eventDto.getPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration())
                .state(EventState.PENDING)
                .build();

        log.trace(DEFAULT_MESSAGE, eventDto, mappedEvent);
        return mappedEvent;
    }

    public static EventResponseDto mapEventToResponseDto(
            Event event,
            long confirmedRequests,
            long views,
            long comments
    ) {

        EventResponseDto mappedResponseDto = EventResponseDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(mapCategoryToResponseDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(mapUserToResponseDto(event.getInitiator()))
                .location(mapEventLocationToResponseDto(event.getLocation()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .views(views)
                .confirmedRequests(confirmedRequests)
                .comments(comments)
                .build();

        log.trace(DEFAULT_MESSAGE, event, mappedResponseDto);
        return mappedResponseDto;
    }

    public static EventShortResponseDto mapEventToShortResponseDto(
            Event event,
            long confirmedRequests,
            long views,
            long comments
    ) {
        EventShortResponseDto mappedShortResponseDto = EventShortResponseDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(mapCategoryToResponseDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(mapUserToShortResponseDto(event.getInitiator()))
                .paid(event.isPaid())
                .views(views)
                .comments(comments)
                .confirmedRequests(confirmedRequests)
                .build();

        log.trace(DEFAULT_MESSAGE, event, mappedShortResponseDto);
        return mappedShortResponseDto;
    }

}
