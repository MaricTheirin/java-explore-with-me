package ru.practicum.ewm.participations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.exception.EventNotAvailableForParticipationException;
import ru.practicum.ewm.events.exception.EventNotEditableException;
import ru.practicum.ewm.events.exception.EventNotFoundException;
import ru.practicum.ewm.events.exception.EventParticipationLimitExceededException;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.participations.dto.ParticipationResponseDto;
import ru.practicum.ewm.participations.dto.ParticipationsStatusUpdateDto;
import ru.practicum.ewm.participations.dto.ParticipationsStatusUpdateResponseDto;
import ru.practicum.ewm.participations.exception.ParticipationNotFoundException;
import ru.practicum.ewm.participations.mapper.ParticipationDtoMapper;
import ru.practicum.ewm.participations.model.Participation;
import ru.practicum.ewm.participations.model.ParticipationState;
import ru.practicum.ewm.participations.repository.ParticipationRepository;
import ru.practicum.ewm.users.exception.UserNotFoundException;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.UserRepository;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.participations.mapper.ParticipationDtoMapper.mapParticipationToResponseDto;
import static ru.practicum.ewm.participations.model.ParticipationState.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {

    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationResponseDto> getUserParticipationRequests(long userId) {
        List<Participation> requestedParticipations = participationRepository.getParticipationsByRequester_Id(userId);
        log.debug("Получен список событий: {}", requestedParticipations);
        return requestedParticipations
                .stream()
                .map(ParticipationDtoMapper::mapParticipationToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationResponseDto createParticipationRequest(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        User requester = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        checkBeforeCreate(userId, event);
        Participation newParticipation = participationRepository.saveAndFlush(
                    Participation.builder()
                    .requester(requester)
                    .event(event)
                    .status(event.isRequestModeration() ? PENDING : CONFIRMED)
                    .build()
        );
        if (newParticipation.getStatus() == CONFIRMED) {
            int updatedConfirmedRequestsCount = event.getConfirmedRequests() + 1;
            log.debug("Количество участников события увеличено с {} до {}",
                    event.getConfirmedRequests(),
                    updatedConfirmedRequestsCount
            );
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }
        log.debug("Создан запрос на участие {}", newParticipation);
        return mapParticipationToResponseDto(newParticipation);
    }

    @Override
    @Transactional
    public ParticipationResponseDto cancelParticipationRequest(long userId, long requestId) {
        Participation savedParticipation = participationRepository
                .getParticipationByIdAndRequester_Id(requestId, userId)
                .orElseThrow(() -> new ParticipationNotFoundException(requestId));
        if (savedParticipation.getStatus() == PENDING) {
            log.debug("Запрос на участие с id = {} отменён", requestId);
            savedParticipation.setStatus(CANCELED);
        }
        return mapParticipationToResponseDto(savedParticipation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationResponseDto> getEventParticipationRequests(long userId, long eventId) {
        if (!eventRepository.existsByInitiator_IdAndId(userId, eventId)) {
            return Collections.emptyList();
        }

        List<Participation> eventParticipations = participationRepository.getParticipationsByEvent_Id(eventId);
        log.debug("Получен список запросов на участие: {}", eventParticipations);
        return eventParticipations
                .stream()
                .map(ParticipationDtoMapper::mapParticipationToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationsStatusUpdateResponseDto updateEventParticipationRequestStatus(
            long userId,
            long eventId,
            ParticipationsStatusUpdateDto statusUpdateDto
    ) {
        Event event = eventRepository
                .findByInitiator_IdAndId(userId, eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new EventParticipationLimitExceededException(event.getId());
        }

        if (!event.isRequestModeration()) {
            throw new EventNotEditableException("Событие не требует модерации запросов на участие");
        }

        if (statusUpdateDto.getStatus() != CONFIRMED && statusUpdateDto.getStatus() != REJECTED) {
            throw new EventNotEditableException("Нельзя установить статус " + statusUpdateDto.getStatus());
        }

        Set<Long> pendingIds = participationRepository
                .findByIdInAndStatus(statusUpdateDto.getRequestIds(), PENDING).stream()
                .map(Participation::getId)
                .collect(Collectors.toSet());

        if (event.getConfirmedRequests() + pendingIds.size() > event.getParticipantLimit()) {
            throw new EventParticipationLimitExceededException(event.getId());
        }

        updateParticipationsState(event, pendingIds, statusUpdateDto.getStatus());

        ParticipationsStatusUpdateResponseDto participationStats = new ParticipationsStatusUpdateResponseDto();

        participationRepository
                .findByEvent_IdAndStatusIn(eventId, Arrays.asList(CONFIRMED, CANCELED, REJECTED))
                .stream()
                .map(ParticipationDtoMapper::mapParticipationToResponseDto)
                .forEach(responseDto -> {
                    if (pendingIds.contains(responseDto.getId())) {
                        responseDto.setStatus(statusUpdateDto.getStatus());
                    }
                    if (statusUpdateDto.getStatus() == CONFIRMED) {
                        participationStats.getConfirmedRequests().add(responseDto);
                    } else {
                        participationStats.getRejectedRequests().add(responseDto);
                    }
                });

        return participationStats;
    }

    private void updateParticipationsState(Event event, Set<Long> participationIds, ParticipationState newState) {
        int previousRequestsCount = event.getConfirmedRequests();
        participationRepository.updateStatusByIdIn(newState, participationIds);
        event.setConfirmedRequests(
                event.getConfirmedRequests() + (newState == CONFIRMED ? 1 : -1) * participationIds.size()
        );

        log.debug(
                "Для запросов {} установлен статус {}. Количество подтверждённых запросов изменилось с {} на {}",
                participationIds,
                newState,
                previousRequestsCount,
                event.getConfirmedRequests()
        );
    }

    private void checkBeforeCreate(long userId, Event event) {

        if (participationRepository.existsByRequester_IdAndEvent_Id(userId, event.getId())) {
            log.warn(
                    "Пользователь с id = {} пытается добавить повторный запрос на участие в событии с id = {}",
                    userId,
                    event.getId()
            );
            throw new EventNotAvailableForParticipationException("Нельзя добавить повторный запрос на участие");
        }

        if (event.getInitiator().getId() == userId) {
            log.warn(
                    "Пользователь с id = {} пытается добавить запрос на участие в собственном событии с id = {}",
                    userId,
                    event.getId()
            );
            throw new EventNotAvailableForParticipationException("Нельзя добавить повторный запрос на участие");
        }

        if (event.getState() == EventState.PENDING) {
            log.warn(
                    "Пользователь с id = {} пытается участвовать в неопубликованном событии с id = {} в статусе {}",
                    userId,
                    event.getId(),
                    event.getState()
            );
            throw new EventNotAvailableForParticipationException("Нельзя участвовать в неопубликованном событии");
        }

        if (event.getParticipantLimit() <= event.getConfirmedRequests()) {
            log.warn("Пользователь с id = {} пытается участвовать в заполненном событии с id = {}", userId, event.getId());
            throw new EventParticipationLimitExceededException(event.getId());
        }

    }
}
