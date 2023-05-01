package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventResponseDto;
import ru.practicum.ewm.events.model.EventSort;
import ru.practicum.ewm.events.service.EventService;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
public class EventPublicController {

    private final EventService eventService;

    @GetMapping
    public List<EventResponseDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Set<Long> categories,
            @RequestParam(required = false) boolean paid,
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now()}") LocalDateTime rangeStart,
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().plusYears(99L)}") LocalDateTime rangeEnd,
            @RequestParam(required = false) boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") EventSort sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        log.info("Запрошен список событий с параметрами: text={}; categories={}; paid={}; rangeStart={}; rangeEnd={}; onlyAvailable = {}; sort = {}; from={}; size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size
        );
        return eventService.getEvents(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size,
                request.getRemoteAddr(),
                request.getRequestURI()
        );
    }

    @GetMapping("/{id}")
    public EventResponseDto getEvent(@PathVariable long id, HttpServletRequest request) {
        log.info("Запрошено событие с id = {}", id);
        return eventService.getEvent(id, request.getRemoteAddr(), request.getRequestURI());
    }

}
