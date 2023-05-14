package ru.practicum.ewm.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CommentResponseDto;
import ru.practicum.ewm.comments.exception.NotAvailableToCommentException;
import ru.practicum.ewm.comments.mapper.CommentDtoMapper;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.comments.repository.CommentRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.service.exception.NotFoundException;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.comments.mapper.CommentDtoMapper.mapCommentToResponseDto;
import static ru.practicum.ewm.comments.mapper.CommentDtoMapper.mapDtoToComment;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public CommentResponseDto createComment(Long userId, Long eventId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(eventId));
        checkBeforeCreate(user, event);

        Comment comment = mapDtoToComment(commentDto, event, user);
        commentRepository.saveAndFlush(comment);
        return mapCommentToResponseDto(comment);
    }

    @Transactional
    @Override
    public CommentResponseDto updateComment(Long userId, Long commentId, CommentDto commentDto) {
        Comment savedComment = commentRepository
                .findByIdAndUser_Id(commentId, userId)
                .orElseThrow(() -> new NotFoundException(commentId));
        checkBeforeUpdate(savedComment);

        log.debug("Текст комментария изменён с {} на {}", savedComment.getText(), commentDto.getText());
        savedComment.setText(commentDto.getText());
        savedComment.setIsUpdated(true);
        return mapCommentToResponseDto(savedComment);
    }

    @Transactional
    @Override
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository
                .findByIdAndUser_Id(commentId, userId)
                .orElseThrow(() -> new NotFoundException(commentId));
        commentRepository.delete(comment);
        log.debug("Комментарий {} удалён", comment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentResponseDto> getComments(Long userId, int from, int size) {
        List<Comment> foundComments =
                commentRepository.findByUser_IdOrderByCreatedAsc(userId, PageRequest.of(from, size));
        log.debug("Найдены комментарии: {}", foundComments);
        return foundComments.stream().map(CommentDtoMapper::mapCommentToResponseDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CommentResponseDto getComment(Long userId, Long commentId) {
        Comment foundComment = commentRepository
                .findByIdAndUser_Id(commentId, userId)
                .orElseThrow(() -> new NotFoundException(commentId));
        log.debug("Найден комментарий: {}", foundComment);
        return mapCommentToResponseDto(foundComment);
    }

    @Transactional
    @Override
    public void adminDeleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException(commentId);
        }
        commentRepository.deleteById(commentId);
        log.debug("Комментарий с id = {} удалён", commentId);
    }

    @Override
    public List<CommentResponseDto> getEventComments(Long eventId, int from, int size) {
        if (!eventRepository.existsByIdAndState(eventId, EventState.PUBLISHED)) {
            throw new NotAvailableToCommentException("Опубликованного события с id = " + eventId + " не существует");
        }

        List<Comment> eventComments =
                commentRepository.findByEvent_IdOrderByCreatedAsc(eventId, PageRequest.of(from, size));
        log.debug("Найдены комментарии: {}", eventComments);
        return eventComments.stream().map(CommentDtoMapper::mapCommentToResponseDto).collect(Collectors.toList());
    }

    private void checkBeforeCreate(User user, Event event) {
        if (user.getId() == event.getInitiator().getId()) {
            throw new NotAvailableToCommentException("Нельзя комментировать своё событие");
        }
        if (!requestRepository.existsByRequester_IdAndEvent_Id(user.getId(), event.getId())) {
            throw new NotAvailableToCommentException("Нельзя комментировать событие, в котором не было участия");
        }
        if (event.getState() != EventState.PUBLISHED && !event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new NotAvailableToCommentException("Нельзя комментировать событие, которое не было проведено");
        }
        if (commentRepository.existsByEvent_IdAndUser_Id(event.getId(), user.getId())) {
            throw new NotAvailableToCommentException("Нельзя прокомментировать событие более 1 раза");
        }
    }

}
