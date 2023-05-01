package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.exception.CategoryNotFoundException;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoryRepository;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.exception.*;
import ru.practicum.ewm.events.mapper.EventDtoMapper;
import ru.practicum.ewm.events.model.*;
import ru.practicum.ewm.events.repository.*;
import ru.practicum.ewm.users.exception.UserNotFoundException;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.ewm.events.mapper.EventDtoMapper.mapDtoToEvent;
import static ru.practicum.ewm.events.mapper.EventDtoMapper.mapEventToResponseDto;
import static ru.practicum.ewm.events.mapper.EventLocationMapper.mapDtoToEventLocation;
import static ru.practicum.ewm.events.model.EventState.*;
import static ru.practicum.ewm.events.model.EventStateAction.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventLocationRepository eventLocationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public List<EventResponseDto> adminGetEvents(
            Set<Long> userIds,
            EventState[] states,
            Set<Long> categoryIds,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    ) {
        List<Event> foundEvents =
                eventRepository.getAdminEvents(userIds, states, categoryIds, rangeStart, rangeEnd, from, size);
        log.debug("Получены события: {}", foundEvents);
        return foundEvents.stream().map(EventDtoMapper::mapEventToResponseDto).collect(Collectors.toList());
    }

    @Override
    public EventResponseDto adminUpdateEvent(long eventId, EventDto eventDto) {
        Event savedEvent = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        checkBeforeAdminUpdate(savedEvent, eventDto);
        return updateEvent(savedEvent, eventDto);
    }

    @Override
    public EventResponseDto userUpdateEvent(long userId, long eventId, EventDto eventDto) {
        Event savedEvent = eventRepository
                .findByInitiator_IdAndId(userId, eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        checkBeforeUserUpdate(savedEvent, eventDto);
        return updateEvent(savedEvent, eventDto);
    }

    @Override
    public List<EventResponseDto> getEvents(
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
        List<Event> foundEvents =
                eventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        log.debug("Получены события: {}", foundEvents);
        return foundEvents.stream().map(EventDtoMapper::mapEventToResponseDto).collect(Collectors.toList());
    }

    @Override
    public EventResponseDto getEvent(long id) {
        Event requestedEvent = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
        log.debug("Получено событие: {}", requestedEvent);
        return mapEventToResponseDto(requestedEvent);
    }

    @Override
    public List<EventShortResponseDto> getEvents(long userId, int from, int size) {
        List<Event> requestedEvents = eventRepository.findAllByInitiator_Id(userId);
        log.debug("Получены события: {}", requestedEvents);
        return requestedEvents.stream().map(EventDtoMapper::mapEventToShortResponseDto).collect(Collectors.toList());
    }

    @Override
    public EventResponseDto getEvent(long userId, long eventId) {
        Event requestedEvent = eventRepository
                .findByInitiator_IdAndId(userId, eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        log.debug("Получено событие: {}", requestedEvent);
        return mapEventToResponseDto(requestedEvent);
    }

    @Override
    public EventResponseDto createEvent(long userId, EventDto eventDto) {
        long categoryId = eventDto.getCategory();
        User initiator = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Category eventCategory =
                categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
        EventLocation eventLocation =
                eventLocationRepository.saveAndFlush(mapDtoToEventLocation(eventDto.getLocation()));

        Event createdEvent = eventRepository.save(mapDtoToEvent(eventDto, initiator, eventCategory, eventLocation));
        log.debug("Создано событие: {}", createdEvent);
        return mapEventToResponseDto(createdEvent);
    }

    private EventResponseDto updateEvent(Event savedEvent, EventDto eventDto) {

        if (eventDto.getAnnotation() != null && !eventDto.getAnnotation().isBlank() && !eventDto.getAnnotation().equals(savedEvent.getAnnotation())) {
            log.debug("Аннотация события изменено с {} на {}", savedEvent.getAnnotation(), eventDto.getAnnotation());
            savedEvent.setAnnotation(eventDto.getAnnotation());
        }

        if (eventDto.getCategory() != 0) {
            Category newCategory = categoryRepository
                    .findById(eventDto.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException(eventDto.getCategory()));
            log.debug("Категория события изменено с {} на {}", savedEvent.getCategory(), newCategory);
            savedEvent.setCategory(newCategory);
        }

        if (eventDto.getDescription() != null && !eventDto.getDescription().isBlank() && !eventDto.getDescription().equals(savedEvent.getDescription())) {
            log.debug("Описание события изменено с {} на {}", savedEvent.getDescription(), eventDto.getDescription());
            savedEvent.setDescription(eventDto.getDescription());
        }

        if (eventDto.getEventDate() != null) {
            log.debug("Дата события изменено с {} на {}", savedEvent.getEventDate(), eventDto.getEventDate());
            savedEvent.setEventDate(eventDto.getEventDate());
        }

        if (eventDto.getLocation() != null) {
            EventLocation newLocation = mapDtoToEventLocation(eventDto.getLocation());
            log.debug("Место события изменено с {} на {}", savedEvent.getLocation(), eventDto.getLocation());
            savedEvent.setLocation(newLocation);
        }

        if (eventDto.isPaid() != savedEvent.isPaid()) {
            log.debug("Требование оплаты изменено с {} на {}", savedEvent.isPaid(), eventDto.isPaid());
            savedEvent.setPaid(eventDto.isPaid());
        }

        if (eventDto.getParticipantLimit() != null && eventDto.getParticipantLimit() != savedEvent.getParticipantLimit()) {
            log.debug(
                    "Лимит участников изменен с {} на {}",
                    savedEvent.getParticipantLimit(),
                    eventDto.getParticipantLimit()
            );
            savedEvent.setParticipantLimit(eventDto.getParticipantLimit());
        }

        if (eventDto.isRequestModeration() != savedEvent.isRequestModeration()) {
            log.debug(
                    "Требование модерации изменено с {} на {}",
                    savedEvent.isRequestModeration(),
                    eventDto.isRequestModeration()
            );
            savedEvent.setRequestModeration(eventDto.isRequestModeration());
        }

        if (eventDto.getTitle() != null && !eventDto.getTitle().isBlank() && !eventDto.getTitle().equals(savedEvent.getTitle())) {
            log.debug("Наименование события изменено с {} на {}", savedEvent.getDescription(), eventDto.getDescription());
            savedEvent.setTitle(eventDto.getTitle());
        }

        if (eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case REJECT_EVENT:
                case CANCEL_REVIEW:
                    savedEvent.setState(CANCELED);
                    break;
                case PUBLISH_EVENT:
                    savedEvent.setState(PUBLISHED);
                    savedEvent.setPublishedOn(LocalDateTime.now());
                    savedEvent.setRequestModeration(true);
                    break;
                case SEND_TO_REVIEW:
                    savedEvent.setState(PENDING);
                    break;
                default: throw new EventValidationException("Неизвестное действие: " + eventDto.getStateAction());
            }
            log.debug("Статус события изменён на {}", savedEvent.getState());
        }

        return mapEventToResponseDto(savedEvent);

    }

    private void checkBeforeAdminUpdate(Event savedEvent, EventDto eventDto) {
        if (eventDto.getEventDate() != null && LocalDateTime.now().plusHours(1).isAfter(eventDto.getEventDate())) {
            log.warn("Нельзя редактировать событие, которое начинается менее, чем через час");
            throw new EventNotEditableException("Событие недоступно для редактирования");
        }
        if (eventDto.getStateAction() == PUBLISH_EVENT && savedEvent.getState() != PENDING) {
            log.warn("Невозможно опубликовать событие {} - его статус отличается от PENDING", savedEvent.getState());
            throw new EventNotEditableException("Нельзя опубликовать событие");
        }
        if (eventDto.getStateAction() == REJECT_EVENT && savedEvent.getState() == PUBLISHED) {
            log.warn("Невозможно отклонить событие {} - его статус отличается от PUBLISHED", savedEvent.getState());
            throw new EventNotEditableException("Нельзя отклонить событие");
        }
    }

    private void checkBeforeUserUpdate(Event savedEvent, EventDto eventDto) {
        LocalDateTime minDate = LocalDateTime.now().plusHours(2);
        if (eventDto.getEventDate() != null && eventDto.getEventDate().isBefore(minDate)) {
            log.warn("Создание события, начинающегося {}, невозможно", eventDto.getEventDate());
            throw new EventValidationException("Событие должно начинаться не раньше " + minDate);
        }
        if (savedEvent.getState() != CANCELED && savedEvent.getState() != PENDING) {
            log.warn("Редактирование события в статусе {} невозможно", savedEvent.getState());
            throw new EventNotEditableException("Событие " + savedEvent.getId() + " нельзя редактировать");
        }
    }

}
