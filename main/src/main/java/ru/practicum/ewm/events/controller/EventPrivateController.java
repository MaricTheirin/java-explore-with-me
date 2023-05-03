package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.EventResponseDto;
import ru.practicum.ewm.events.dto.EventShortResponseDto;
import ru.practicum.ewm.events.exception.EventValidationException;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.service.validation.Create;
import ru.practicum.ewm.service.validation.Update;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventPrivateController {

    private final EventService eventService;

    @GetMapping("/users/{userId}/events")
    public List<EventShortResponseDto> getEvents(
            @PathVariable @Positive long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @PositiveOrZero int size
    ) {
        log.info("Запрошен список событий пользователя с id = {} с параметрами: from={}; size={}", userId, from, size);
        return eventService.getEvents(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventResponseDto getEvent(
            @PathVariable @Positive long userId,
            @PathVariable @Positive long eventId
    ) {
        log.info("Запрошена информация о событии с id = {} пользователем с id = {}", eventId, userId);
        return eventService.getEvent(userId, eventId);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDto createEvent(
            @PathVariable @Positive long userId,
            @RequestBody @Validated(value = Create.class) EventDto eventDto
    ) {
        log.info("Пользователь с id = {} запросил создание события {}", userId, eventDto);
        checkEventDate(eventDto.getEventDate());
        return eventService.createEvent(userId, eventDto);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto updateEvent(
            @PathVariable @Positive long userId,
            @PathVariable @Positive long eventId,
            @RequestBody @Validated (value = Update.class) EventDto eventDto
    ) {
        log.info("Пользователь с id = {} запросил обновление события с id = {} на {}", userId, eventId, eventDto);
        checkEventDate(eventDto.getEventDate());
        return eventService.userUpdateEvent(userId, eventId, eventDto);
    }

    private void checkEventDate(LocalDateTime eventDate) {
        //Изначально было реализовано через @Future, но тесты не проходили, т.к. ошибка LocalDateTime
        // должна выдавать 409, а прочие ValidationException - 400.
        if (eventDate != null && LocalDateTime.now().plusHours(2).isAfter(eventDate)) {
            throw new EventValidationException("Дата события задана некорректно");
        }
    }

}
