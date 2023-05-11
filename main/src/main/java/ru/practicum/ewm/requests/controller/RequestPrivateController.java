package ru.practicum.ewm.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.requests.dto.RequestResponseDto;
import ru.practicum.ewm.requests.dto.RequestStatusUpdateDto;
import ru.practicum.ewm.requests.dto.RequestStatusUpdateResponseDto;
import ru.practicum.ewm.requests.service.RequestService;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@Slf4j
@Validated
@RequiredArgsConstructor
public class RequestPrivateController {

    private final RequestService requestService;

    @GetMapping("/requests")
    public List<RequestResponseDto> getParticipationRequests(@PathVariable @Positive long userId) {
        log.info("Пользователь с id = {} запросил список своих заявок на участие в событиях", userId);
        return requestService.getUserParticipationRequests(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestResponseDto createParticipationRequest(
            @PathVariable @Positive long userId,
            @RequestParam @Positive long eventId
    ) {
        log.info("Пользователь с id = {} пытается записаться на участие в событие с id = {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public RequestResponseDto cancelParticipationRequest(
            @PathVariable @Positive long userId,
            @PathVariable @Positive long requestId
    ) {
        log.info("Пользователь с id = {} пытается отменить запрос на участие с id = {}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<RequestResponseDto> getEventParticipationRequests(
            @PathVariable @Positive long userId,
            @PathVariable @Positive long eventId
    ) {
        log.info("Пользователь с id = {} запросил информацию об участниках своего события с id = {}", userId, eventId);
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public RequestStatusUpdateResponseDto updateEventParticipationRequestStatus(
            @PathVariable @Positive long userId,
            @PathVariable @Positive long eventId,
            @RequestBody @Valid RequestStatusUpdateDto statusUpdateDto
    ) {
        log.info(
                "Пользователь с id = {} выполняет обновление участников события с id = {}, переданы данные: {}",
                userId,
                eventId,
                statusUpdateDto
        );
        return requestService.updateEventRequestStatus(userId, eventId, statusUpdateDto);
    }

}
