package ru.practicum.ewm.comments.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CommentResponseDto;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.service.mapper.Mapper;
import ru.practicum.ewm.users.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class CommentDtoMapper extends Mapper {

    public static Comment mapDtoToComment(CommentDto commentDto, Event event, User user) {
        Comment mappedComment = Comment.builder()
                .event(event)
                .user(user)
                .text(commentDto.getText())
                .isUpdated(false)
                .build();
        log.trace(DEFAULT_MESSAGE, commentDto, mappedComment);
        return mappedComment;
    }

    public static CommentResponseDto mapCommentToResponseDto(Comment comment) {
        CommentResponseDto mappedComment = CommentResponseDto.builder()
                .id(comment.getId())
                .created(comment.getCreated())
                .isUpdated(comment.getIsUpdated())
                .updated(comment.getUpdated())
                .text(comment.getText())
                .author(comment.getUser().getName())
                .build();

        log.trace(DEFAULT_MESSAGE, comment, mappedComment);
        return mappedComment;
    }

}
