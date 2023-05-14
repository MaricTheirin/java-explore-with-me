package ru.practicum.ewm.comments.service;

import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CommentResponseDto;
import java.util.List;

public interface CommentService {

    CommentResponseDto createComment(Long userId, Long eventId, CommentDto commentDto);

    CommentResponseDto updateComment(Long userId, Long commentId, CommentDto commentDto);

    void deleteComment(Long userId, Long commentId);

    List<CommentResponseDto> getComments(Long userId, int from, int size);

    CommentResponseDto getComment(Long userId, Long commentId);

    void adminDeleteComment(Long commentId);

    List<CommentResponseDto> getEventComments(Long eventId, int from, int size);

}
