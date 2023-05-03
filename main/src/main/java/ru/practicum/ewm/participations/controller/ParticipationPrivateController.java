package ru.practicum.ewm.participations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.participations.dto.ParticipationResponseDto;
import ru.practicum.ewm.participations.dto.ParticipationsStatusUpdateDto;
import ru.practicum.ewm.participations.dto.ParticipationsStatusUpdateResponseDto;
import ru.practicum.ewm.participations.service.ParticipationService;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ParticipationPrivateController {

    private final ParticipationService participationService;

    @GetMapping("/requests")
    public List<ParticipationResponseDto> getParticipationRequests(@PathVariable @Positive long userId) {
        log.info("Пользователь с id = {} запросил список своих заявок на участие в событиях", userId);
        return participationService.getUserParticipationRequests(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationResponseDto createParticipationRequest(
            @PathVariable @Positive long userId,
            @RequestParam @Positive long eventId
    ) {
        log.info("Пользователь с id = {} пытается записаться на участие в событие с id = {}", userId, eventId);
        return participationService.createParticipationRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationResponseDto cancelParticipationRequest(
            @PathVariable @Positive long userId,
            @PathVariable @Positive long requestId
    ) {
        log.info("Пользователь с id = {} пытается отменить запрос на участие с id = {}", userId, requestId);
        return participationService.cancelParticipationRequest(userId, requestId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationResponseDto> getEventParticipationRequests(
            @PathVariable @Positive long userId,
            @PathVariable @Positive long eventId
    ) {
        log.info("Пользователь с id = {} запросил информацию об участниках своего события с id = {}", userId, eventId);
        return participationService.getEventParticipationRequests(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public ParticipationsStatusUpdateResponseDto updateEventParticipationRequestStatus(
            @PathVariable @Positive long userId,
            @PathVariable @Positive long eventId,
            @RequestBody @Validated ParticipationsStatusUpdateDto statusUpdateDto
    ) {
        log.info(
                "Пользователь с id = {} выполняет обновление участников события с id = {}, переданы данные: {}",
                userId,
                eventId,
                statusUpdateDto
        );
        return participationService.updateEventParticipationRequestStatus(userId, eventId, statusUpdateDto);
    }

}
