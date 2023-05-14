package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CommentResponseDto;
import ru.practicum.ewm.comments.service.CommentService;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentPrivateController {

    private final CommentService commentService;

    @GetMapping
    List<CommentResponseDto> getComments(
            @PathVariable @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("Пользователь с id = {} запросил список своих комментариев с разбивкой from = {}, size = {}", userId, from, size);
        return commentService.getComments(userId, from, size);
    }

    @GetMapping("/{commentId}")
    CommentResponseDto getComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId
    ) {
        log.info("Пользователь с id = {} запросил свой комментарий с id = {}", userId, commentId);
        return commentService.getComment(userId, commentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CommentResponseDto createComment(
            @PathVariable @Positive Long userId,
            @RequestParam @Positive Long eventId,
            @RequestBody @Valid CommentDto commentDto
    ) {
        log.info("Пользователь с id = {} добавляет к событию с id = {} комментарий: {}", userId, eventId, commentDto);
        return commentService.createComment(userId, eventId, commentDto);
    }

    @PatchMapping("/{commentId}")
    CommentResponseDto updateComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId,
            @RequestBody @Valid CommentDto commentDto
    ) {
        log.info("Пользователь с id = {} обновляет комментарий c id = {}: {}",
                userId,
                commentId,
                commentDto
        );
        return commentService.updateComment(userId, commentId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId
    ) {
        log.info("Пользователь с id = {} удаляет комментарий с id = {}", userId, commentId);
        commentService.deleteComment(userId, commentId);
    }

}
