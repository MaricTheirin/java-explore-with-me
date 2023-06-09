package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventResponseDto;
import ru.practicum.ewm.events.dto.EventUpdateDto;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.service.EventService;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@Validated
@RequiredArgsConstructor
public class EventAdminController {

    private final EventService eventService;

    @GetMapping
    public List<EventResponseDto> adminGetEvents(
            @RequestParam(name = "users") Set<Long> userIds,
            @RequestParam(required = false) EventState[] states,
            @RequestParam(name = "categories") Set<Long> categoryIds,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("Запрошен список событий с параметрами: userIds={}; states={}; categoryIds={}; rangeStart={}; rangeEnd={}; from={}; size={}",
                userIds, states, categoryIds, rangeStart, rangeEnd, from, size
        );
        return eventService.adminGetEvents(userIds, states, categoryIds, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventResponseDto adminUpdateEvent(
            @PathVariable @Positive long eventId,
            @RequestBody @Valid EventUpdateDto eventDto
    ) {
        log.info("Запрошено обновление события с id = {} на {}", eventId, eventDto);
        return eventService.adminUpdateEvent(eventId, eventDto);
    }

}
