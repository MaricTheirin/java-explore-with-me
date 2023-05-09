package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoryRepository;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.exception.*;
import ru.practicum.ewm.events.model.*;
import ru.practicum.ewm.events.repository.*;
import ru.practicum.ewm.requests.model.ConfirmedRequestCount;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.service.exception.NotFoundException;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.UserRepository;
import ru.practicum.ewm.statistic.client.StatisticClient;
import ru.practicum.ewm.statistic.dto.EndpointHitDto;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.events.mapper.EventDtoMapper.*;
import static ru.practicum.ewm.events.mapper.EventLocationMapper.mapDtoToEventLocation;
import static ru.practicum.ewm.events.model.EventState.*;
import static ru.practicum.ewm.events.model.EventStateAction.*;
import static ru.practicum.ewm.requests.model.RequestState.CONFIRMED;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventLocationRepository eventLocationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StatisticClient statisticClient;

    @Value("${app.name}")
    private String APPLICATION_NAME;

    @Override
    @Transactional(readOnly = true)
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
        return getConfirmedRequestsAndMapToResponseDtos(foundEvents);
    }

    @Override
    @Transactional
    public EventResponseDto adminUpdateEvent(long eventId, EventDto eventDto) {
        Event savedEvent = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(eventId));
        checkBeforeAdminUpdate(savedEvent, eventDto);
        return updateEvent(savedEvent, eventDto);
    }

    @Override
    @Transactional
    public EventResponseDto userUpdateEvent(long userId, long eventId, EventDto eventDto) {
        Event savedEvent = eventRepository
                .findByInitiator_IdAndId(userId, eventId).orElseThrow(() -> new NotFoundException(eventId));
        checkBeforeUserUpdate(savedEvent, eventDto);
        return updateEvent(savedEvent, eventDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getEvents(
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
    ) {
        createHitToStatisticService(ip, url);

        List<Event> foundEvents =
                eventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        log.debug("Результат: {}", foundEvents);
        return getConfirmedRequestsAndMapToResponseDtos(foundEvents);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDto getEvent(long id, String ip, String url) {
        createHitToStatisticService(ip, url);

        Event requestedEvent = eventRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        log.debug("Получено событие: {}", requestedEvent);
        return getConfirmedRequestsAndMapToResponseDto(requestedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortResponseDto> getEvents(long userId, int from, int size) {
        List<Event> requestedEvents = eventRepository.findAllByInitiator_Id(userId, PageRequest.of(from, size));
        log.debug("Получены события: {}", requestedEvents);
        return getConfirmedRequestsAndMapToShortResponseDtos(requestedEvents);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDto getEvent(long userId, long eventId) {
        Event requestedEvent = eventRepository
                .findByInitiator_IdAndId(userId, eventId).orElseThrow(() -> new NotFoundException(eventId));
        log.debug("Получено событие: {}", requestedEvent);
        return getConfirmedRequestsAndMapToResponseDto(requestedEvent);
    }

    @Override
    @Transactional
    public EventResponseDto createEvent(long userId, EventDto eventDto) {
        long categoryId = eventDto.getCategory();
        User initiator = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        Category eventCategory =
                categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException(categoryId));
        EventLocation eventLocation =
                eventLocationRepository.saveAndFlush(mapDtoToEventLocation(eventDto.getLocation()));

        Event createdEvent = eventRepository.save(mapDtoToEvent(eventDto, initiator, eventCategory, eventLocation));
        log.debug("Создано событие: {}", createdEvent);
        return mapEventToResponseDto(createdEvent, 0);
    }

    private EventResponseDto updateEvent(Event savedEvent, EventDto eventDto) {

        if (eventDto.getAnnotation() != null && !eventDto.getAnnotation().isBlank() && !eventDto.getAnnotation().equals(savedEvent.getAnnotation())) {
            log.debug("Аннотация события изменено с {} на {}", savedEvent.getAnnotation(), eventDto.getAnnotation());
            savedEvent.setAnnotation(eventDto.getAnnotation());
        }

        if (eventDto.getCategory() != null) {
            Category newCategory = categoryRepository
                    .findById(eventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException(eventDto.getCategory()));
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
            EventLocation newLocation =
                    eventLocationRepository.saveAndFlush(mapDtoToEventLocation(eventDto.getLocation()));
            log.debug("Место события изменено с {} на {}", savedEvent.getLocation(), eventDto.getLocation());
            savedEvent.setLocation(newLocation);
        }

        if (eventDto.getPaid() != null && eventDto.getPaid() != savedEvent.isPaid()) {
            log.debug("Требование оплаты изменено с {} на {}", savedEvent.isPaid(), eventDto.getPaid());
            savedEvent.setPaid(eventDto.getPaid());
        }

        if (eventDto.getParticipantLimit() != null && eventDto.getParticipantLimit() != savedEvent.getParticipantLimit()) {
            log.debug(
                    "Лимит участников изменен с {} на {}",
                    savedEvent.getParticipantLimit(),
                    eventDto.getParticipantLimit()
            );
            savedEvent.setParticipantLimit(eventDto.getParticipantLimit());
        }

        if (eventDto.getRequestModeration() != null && eventDto.getRequestModeration() != savedEvent.isRequestModeration()) {
            log.debug(
                    "Требование модерации изменено с {} на {}",
                    savedEvent.isRequestModeration(),
                    eventDto.getRequestModeration()
            );
            savedEvent.setRequestModeration(eventDto.getRequestModeration());
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
                    break;
                case SEND_TO_REVIEW:
                    savedEvent.setState(PENDING);
                    break;
                default: throw new EventValidationException("Неизвестное действие: " + eventDto.getStateAction());
            }
            log.debug("Статус события изменён на {}", savedEvent.getState());
        }

        return getConfirmedRequestsAndMapToResponseDto(savedEvent);
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

    private void createHitToStatisticService(String ip, String url) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(APPLICATION_NAME)
                .uri(url)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();

        try {
            statisticClient.saveHit(endpointHitDto);
            log.debug("Информация о запросе отправлена на сервис статистики: {}", endpointHitDto);
        } catch (Exception e) {
            log.warn("Ошибка при отправке информации на сервис статистики: {}", e.getMessage());
        }
    }

    private EventResponseDto getConfirmedRequestsAndMapToResponseDto(Event event) {
        return mapEventToResponseDto(event, requestRepository.countByEvent_IdAndStatus(event.getId(), CONFIRMED));
    }

    private List<EventResponseDto> getConfirmedRequestsAndMapToResponseDtos(List<Event> events) {
        Map<Long, Integer> confirmedRequests = getConfirmedRequestsCount(events);
        return events
                .stream()
                .map(event -> mapEventToResponseDto(event, confirmedRequests.getOrDefault(event.getId(), 0)))
                .collect(Collectors.toList());
    }

    private List<EventShortResponseDto> getConfirmedRequestsAndMapToShortResponseDtos(List<Event> events) {
        Map<Long, Integer> confirmedRequests = getConfirmedRequestsCount(events);
        return events
                .stream()
                .map(event -> mapEventToShortResponseDto(event, confirmedRequests.getOrDefault(event.getId(), 0)))
                .collect(Collectors.toList());
    }

    private Map<Long, Integer> getConfirmedRequestsCount(List<Event> events) {
        return getConfirmedRequestsCount(events.stream().map(Event::getId).collect(Collectors.toList()));
    }

    public Map<Long, Integer> getConfirmedRequestsCount(Collection<Long> eventIds) {
        Map<Long, Integer> confirmedRequestCounts = requestRepository
                .countAllByEvent_IdInAndStatusIs(eventIds, CONFIRMED)
                .stream()
                .collect(Collectors.toMap(ConfirmedRequestCount::getEventId, ConfirmedRequestCount::getConfirmed));
        log.trace("Получены списки подтверждённых запросов: {}", confirmedRequestCounts);
        return confirmedRequestCounts;
    }

}
