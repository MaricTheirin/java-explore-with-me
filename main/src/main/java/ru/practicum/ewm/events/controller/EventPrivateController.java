package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.EventResponseDto;
import ru.practicum.ewm.events.dto.EventShortResponseDto;
import ru.practicum.ewm.events.service.EventService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class EventPrivateController {

    private final EventService eventService;

    @GetMapping("/users/{userId}/events")
    public List<EventShortResponseDto> getEvents(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Запрошен список событий пользователя с id = {} с параметрами: from={}; size={}", userId, from, size);
        return eventService.getEvents(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventResponseDto getEvent(
            @PathVariable long userId,
            @PathVariable long eventId
    ) {
        log.info("Запрошена информация о событии с id = {} пользователем с id = {}", eventId, userId);
        return eventService.getEvent(userId, eventId);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDto createEvent(
            @PathVariable long userId,
            @RequestBody EventDto eventDto
    ) {
        log.info("Пользователь с id = {} запросил создание события {}", userId, eventDto);
        return eventService.createEvent(userId, eventDto);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto updateEvent(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestBody EventDto eventDto
    ) {
        log.info("Пользователь с id = {} запросил обновление события с id = {} на {}", userId, eventId, eventDto);
        return eventService.userUpdateEvent(userId, eventId, eventDto);
    }

}
