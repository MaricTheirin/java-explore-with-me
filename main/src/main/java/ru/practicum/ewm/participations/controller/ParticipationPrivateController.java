package ru.practicum.ewm.participations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.participations.dto.ParticipationResponseDto;
import ru.practicum.ewm.participations.service.ParticipationService;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@Slf4j
@RequiredArgsConstructor
public class ParticipationPrivateController {

    private final ParticipationService participationService;

    @GetMapping
    public List<ParticipationResponseDto> getParticipationRequests(@PathVariable long userId) {
        log.info("Пользователь с id = {} запросил список своих заявок на участие в событиях", userId);
        return participationService.getUserParticipationRequests(userId);
    }

    /*
    нельзя добавить повторный запрос (Ожидается код ошибки 409)
    инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
    если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
     */

    @PostMapping
    public ParticipationResponseDto createParticipationRequest(
            @PathVariable long userId,
            @RequestParam long eventId
    ) {
        log.info("Пользователь с id = {} пытается записаться на участие в событие с id = {}", userId, eventId);
        return participationService.createParticipationRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationResponseDto cancelParticipationRequest(@PathVariable long userId, @PathVariable long requestId) {
        log.info("Пользователь с id = {} пытается отменить запрос на участие с id = {}", userId, requestId);
        return participationService.cancelParticipationRequest(userId, requestId);
    }

}
