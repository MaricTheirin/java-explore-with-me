package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventResponseDto;
import ru.practicum.ewm.events.model.EventSort;
import ru.practicum.ewm.events.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
public class EventPublicController {

    private final EventService eventService;


    /*
    информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
     */

    @GetMapping
    public List<EventResponseDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Set<Long> categories,
            @RequestParam boolean paid,
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now()}") LocalDateTime rangeStart,
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).MAX}") LocalDateTime rangeEnd,
            @RequestParam boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") EventSort sort,
            @RequestParam int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Запрошен список событий с параметрами: text={}; categories={}; paid={}; rangeStart={}; rangeEnd={}; onlyAvailable = {}; sort = {}; from={}; size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size
        );
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventResponseDto getEvent(@PathVariable long id) {
        log.info("Запрошено событие с id = {}", id);
        return eventService.getEvent(id);
    }

}
