package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoryRepository;
import ru.practicum.ewm.comments.model.CommentsCount;
import ru.practicum.ewm.comments.repository.CommentRepository;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.exception.*;
import ru.practicum.ewm.events.model.*;
import ru.practicum.ewm.events.repository.*;
import ru.practicum.ewm.requests.model.ConfirmedRequestCount;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.service.exception.NotFoundException;
import ru.practicum.ewm.statistic.dto.EndpointHitsResultDto;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.UserRepository;
import ru.practicum.ewm.statistic.client.StatisticClient;
import ru.practicum.ewm.statistic.dto.EndpointHitDto;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
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
    private final CommentRepository commentRepository;
    private final EventLocationRepository eventLocationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final StatisticClient statisticClient;

    @Value("${app.name}")
    private String applicationName;

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
        return getStatsAndMapToResponseDtos(foundEvents);
    }

    @Override
    @Transactional
    public EventResponseDto adminUpdateEvent(long eventId, EventUpdateDto eventDto) {
        Event savedEvent = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(eventId));
        checkBeforeAdminUpdate(savedEvent, eventDto);
        return updateEvent(savedEvent, eventDto);
    }

    @Override
    @Transactional
    public EventResponseDto userUpdateEvent(long userId, long eventId, EventUpdateDto eventDto) {
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
            Boolean paid,
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
        Comparator<EventResponseDto> sortByDate = Comparator.comparing(EventResponseDto::getEventDate).reversed();
        Comparator<EventResponseDto> sortByViews = Comparator.comparing(EventResponseDto::getViews).reversed();
        Predicate<EventResponseDto> filterOnlyAvailable =
                e -> e.getParticipantLimit() == 0 || e.getConfirmedRequests() < e.getParticipantLimit();

        List<Event> events =
                eventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        log.trace("Результат запроса из репозитория: {}", events);

        List<EventResponseDto> mappedEvents = getStatsAndMapToResponseDtos(events)
                .stream()
                .filter(onlyAvailable ? filterOnlyAvailable : y -> true)
                .sorted(sort == EventSort.EVENT_DATE ? sortByDate : sortByViews)
                .collect(Collectors.toList());
        log.debug("Результат: {}", mappedEvents);
        return mappedEvents;
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDto getEvent(long id, String ip, String url) {
        createHitToStatisticService(ip, url);

        Event requestedEvent = eventRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        log.debug("Получено событие: {}", requestedEvent);
        return getStatsAndMapToResponseDto(requestedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortResponseDto> getEvents(long userId, int from, int size) {
        List<Event> requestedEvents = eventRepository.findAllByInitiator_Id(userId, PageRequest.of(from, size));
        log.debug("Получены события: {}", requestedEvents);
        return getStatsAndMapToShortResponseDtos(requestedEvents);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDto getEvent(long userId, long eventId) {
        Event requestedEvent = eventRepository
                .findByInitiator_IdAndId(userId, eventId).orElseThrow(() -> new NotFoundException(eventId));
        log.debug("Получено событие: {}", requestedEvent);
        return getStatsAndMapToResponseDto(requestedEvent);
    }

    @Override
    @Transactional
    public EventResponseDto createEvent(long userId, EventCreateDto eventDto) {
        long categoryId = eventDto.getCategory();
        User initiator = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        Category eventCategory =
                categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException(categoryId));
        EventLocation eventLocation =
                eventLocationRepository.saveAndFlush(mapDtoToEventLocation(eventDto.getLocation()));

        Event createdEvent = eventRepository.save(mapDtoToEvent(eventDto, initiator, eventCategory, eventLocation));
        log.debug("Создано событие: {}", createdEvent);
        return mapEventToResponseDto(createdEvent, 0, 0L, 0L);
    }

    private EventResponseDto updateEvent(Event savedEvent, EventUpdateDto eventDto) {

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

        if (eventDto.getDescription() != null && !eventDto.getDescription().isBlank()) {
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

        return getStatsAndMapToResponseDto(savedEvent);
    }

    private void checkBeforeAdminUpdate(Event savedEvent, EventUpdateDto eventDto) {
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

    private void checkBeforeUserUpdate(Event savedEvent, EventUpdateDto eventDto) {
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
                .app(applicationName)
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

    private EventResponseDto getStatsAndMapToResponseDto(Event event) {
        return mapEventToResponseDto(
                event,
                requestRepository.countByEvent_IdAndStatus(event.getId(), CONFIRMED),
                getEventsViewsCount(List.of(event.getId())).getOrDefault(event.getId(), 0L),
                commentRepository.countByEvent_Id(event.getId())
        );
    }

    private List<EventResponseDto> getStatsAndMapToResponseDtos(Collection<Event> events) {
        Collection<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = getConfirmedRequestsCount(eventIds);
        Map<Long, Long> views = getEventsViewsCount(eventIds);
        Map<Long, Long> comments = getCommentsCount(eventIds);

        return events
                .stream()
                .map(event -> mapEventToResponseDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L),
                        comments.getOrDefault(event.getId(), 0L))
                ).collect(Collectors.toList());
    }

    public List<EventShortResponseDto> getStatsAndMapToShortResponseDtos(Collection<Event> events) {
        Collection<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = getConfirmedRequestsCount(eventIds);
        Map<Long, Long> views = getEventsViewsCount(eventIds);
        Map<Long, Long> comments = getCommentsCount(eventIds);

        return events
                .stream()
                .map(event -> mapEventToShortResponseDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L),
                        comments.getOrDefault(event.getId(), 0L))
                ).collect(Collectors.toList());
    }

    private Map<Long, Long> getConfirmedRequestsCount(Collection<Long> eventIds) {
        Map<Long, Long> confirmedRequestCounts = requestRepository
                .countAllByEvent_IdInAndStatusIs(eventIds, CONFIRMED)
                .stream()
                .collect(Collectors.toMap(ConfirmedRequestCount::getEventId, ConfirmedRequestCount::getConfirmed));
        log.trace("Получены списки подтверждённых запросов: {}", confirmedRequestCounts);
        return confirmedRequestCounts;
    }

    private Map<Long, Long> getCommentsCount(Collection<Long> eventIds) {
        Map<Long, Long> commentsCount = commentRepository
                .countAllByEvent_Id(eventIds)
                .stream()
                .collect(Collectors.toMap(CommentsCount::getEventId, CommentsCount::getComments));
        log.trace("Получены списки подтверждённых запросов: {}", commentsCount);
        return commentsCount;
    }

    private Map<Long, Long> getEventsViewsCount(Collection<Long> eventsUris) {
        Map<String, Long> uriToGetStats = eventsUris
                .stream()
                .collect(Collectors.toMap(x -> "/events/".concat(Long.toString(x)), y -> y));
        try {
            List<EndpointHitsResultDto> hits = statisticClient.getStats(
                    LocalDateTime.now().minusYears(1),
                    LocalDateTime.now(),
                    new ArrayList<>(uriToGetStats.keySet())
            );
            log.trace("Получена статистика просмотров: {}", hits);

            Map<Long, Long> viewStats = hits
                    .stream()
                    .collect(Collectors.toMap(x -> uriToGetStats.get(x.getUri()), EndpointHitsResultDto::getHits));
            log.trace("Статистика просмотров в разрезе URL: {}", viewStats);
            return viewStats;
        } catch (Exception e) {
            log.warn("При получении информации из сервиса статистики произошла ошибка: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

}
