package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentResponseDto;
import ru.practicum.ewm.comments.service.CommentService;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping("/events/{eventId}/comments")
    List<CommentResponseDto> getEventComments(
            @PathVariable @Positive Long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "20") @Positive int size
    ) {
        log.info("Запрошен список комментариев к событию с id = {} с параметрами from = {}, size = {}", eventId, from, size);
        return commentService.getEventComments(eventId, from, size);
    }

}
