package ru.practicum.ewm.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.exception.EventNotAvailableForParticipationException;
import ru.practicum.ewm.events.exception.EventNotEditableException;
import ru.practicum.ewm.events.exception.EventParticipationLimitExceededException;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.requests.dto.RequestResponseDto;
import ru.practicum.ewm.requests.dto.RequestStatusUpdateDto;
import ru.practicum.ewm.requests.dto.RequestStatusUpdateResponseDto;
import ru.practicum.ewm.requests.mapper.RequestDtoMapper;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.model.RequestState;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.service.exception.NotFoundException;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.UserRepository;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.requests.mapper.RequestDtoMapper.mapRequestToResponseDto;
import static ru.practicum.ewm.requests.model.RequestState.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RequestResponseDto> getUserParticipationRequests(long userId) {
        List<Request> requests = requestRepository.getRequestsByRequester_Id(userId);
        log.debug("Получен список событий: {}", requests);
        return requests
                .stream()
                .map(RequestDtoMapper::mapRequestToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestResponseDto createRequest(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(eventId));
        User requester = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        checkBeforeCreate(userId, event);
        Request newRequest = requestRepository.saveAndFlush(
                    Request.builder()
                    .requester(requester)
                    .event(event)
                    .status(event.isRequestModeration() ? PENDING : CONFIRMED)
                    .build()
        );
        log.debug("Создан запрос на участие {}", newRequest);
        return mapRequestToResponseDto(newRequest);
    }

    @Override
    @Transactional
    public RequestResponseDto cancelRequest(long userId, long requestId) {
        Request savedRequest = requestRepository
                .getRequestByIdAndRequester_Id(requestId, userId)
                .orElseThrow(() -> new NotFoundException(requestId));
        if (savedRequest.getStatus() != CANCELED) {
            log.debug("Запрос на участие с id = {} отменён", requestId);
            savedRequest.setStatus(CANCELED);
        }
        return mapRequestToResponseDto(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestResponseDto> getEventRequests(long userId, long eventId) {
        if (!eventRepository.existsByInitiator_IdAndId(userId, eventId)) {
            return Collections.emptyList();
        }

        List<Request> eventRequests = requestRepository.getRequestsByEvent_Id(eventId);
        log.debug("Получен список запросов на участие: {}", eventRequests);
        return eventRequests
                .stream()
                .map(RequestDtoMapper::mapRequestToResponseDto)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public RequestStatusUpdateResponseDto updateEventRequestStatus(
            long userId,
            long eventId,
            RequestStatusUpdateDto statusUpdateDto
    ) {
        Event event = eventRepository
                .findByInitiator_IdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(eventId));

        int confirmedRequests = requestRepository.countByEvent_IdAndStatus(eventId, CONFIRMED);

        if (confirmedRequests >= event.getParticipantLimit()) {
            throw new EventParticipationLimitExceededException(event.getId());
        }

        if (!event.isRequestModeration()) {
            throw new EventNotEditableException("Событие не требует модерации запросов на участие");
        }

        if (statusUpdateDto.getStatus() != CONFIRMED && statusUpdateDto.getStatus() != REJECTED) {
            throw new EventNotEditableException("Нельзя установить статус " + statusUpdateDto.getStatus());
        }

        Set<Long> pendingIds = requestRepository
                .findByIdInAndStatus(statusUpdateDto.getRequestIds(), PENDING).stream()
                .map(Request::getId)
                .collect(Collectors.toSet());

        if (confirmedRequests + pendingIds.size() > event.getParticipantLimit()) {
            throw new EventParticipationLimitExceededException(event.getId());
        }

        updateRequestsState(pendingIds, statusUpdateDto.getStatus());

        RequestStatusUpdateResponseDto requestStatus = new RequestStatusUpdateResponseDto();

        requestRepository
                .findByEvent_IdAndStatusIn(eventId, Arrays.asList(CONFIRMED, CANCELED, REJECTED))
                .stream()
                .map(RequestDtoMapper::mapRequestToResponseDto)
                .forEach(responseDto -> {
                    if (pendingIds.contains(responseDto.getId())) {
                        responseDto.setStatus(statusUpdateDto.getStatus());
                    }
                    if (statusUpdateDto.getStatus() == CONFIRMED) {
                        requestStatus.getConfirmedRequests().add(responseDto);
                    } else {
                        requestStatus.getRejectedRequests().add(responseDto);
                    }
                });

        return requestStatus;
    }

    private void updateRequestsState(Set<Long> requestIds, RequestState newState) {
        requestRepository.updateStatusByIdIn(newState, requestIds);
        log.debug("Для запросов {} установлен статус {}", requestIds, newState);
    }

    private void checkBeforeCreate(long userId, Event event) {

        if (requestRepository.existsByRequester_IdAndEvent_Id(userId, event.getId())) {
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

        if (event.getParticipantLimit() != 0 &&
            event.getParticipantLimit() <= requestRepository.countByEvent_IdAndStatus(event.getId(), CONFIRMED)
        ) {
            log.warn("Пользователь с id = {} пытается участвовать в заполненном событии с id = {}", userId, event.getId());
            throw new EventParticipationLimitExceededException(event.getId());
        }

    }
}
