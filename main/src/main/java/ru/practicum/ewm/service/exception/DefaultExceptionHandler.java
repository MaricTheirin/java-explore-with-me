package ru.practicum.ewm.service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(EwmException.class)
    protected ResponseEntity<ExceptionMessage> handleEwmException(
            EwmException exception,
            HttpServletRequest request
    ) {
        logException(exception, request);
        return new ResponseEntity<>(
                new ExceptionMessage(exception.getMessage(), request.getRequestURI(), exception.getStatus().name()),
                exception.getStatus()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ExceptionMessage> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        HttpStatus resultStatus = HttpStatus.BAD_REQUEST;
        logException(exception, request);
        String errorMessage = exception.getFieldErrors().stream()
                .map(error -> "'" + error.getField() + "': " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return new ResponseEntity<>(
                new ExceptionMessage("Ошибка при проверке полей: " + errorMessage, request.getRequestURI(), resultStatus.name()),
                resultStatus
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ExceptionMessage> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        HttpStatus resultStatus = HttpStatus.BAD_REQUEST;
        logException(exception, request);
        return new ResponseEntity<>(
                new ExceptionMessage("Заданы не все требуемые параметры: " + exception.getMessage(), request.getRequestURI(), resultStatus.name()),
                resultStatus
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ExceptionMessage> handleConstraintViolationException(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        HttpStatus resultStatus = HttpStatus.CONFLICT;
        logException(exception, request);
        String errorMessage = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessageTemplate)
                .collect(Collectors.joining("; "));
        return new ResponseEntity<>(
                new ExceptionMessage("Ошибка при проверке: " + errorMessage, request.getRequestURI(), resultStatus.name()),
                resultStatus
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ExceptionMessage> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        HttpStatus resultStatus = HttpStatus.BAD_REQUEST;
        logException(exception, request);
        return new ResponseEntity<>(
                new ExceptionMessage(exception.getMessage(), request.getRequestURI(), resultStatus.name()),
                resultStatus
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ExceptionMessage> handleIllegalArgumentException(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        HttpStatus resultStatus = HttpStatus.BAD_REQUEST;
        logException(exception, request);
        return new ResponseEntity<>(
                new ExceptionMessage(exception.getMostSpecificCause().getMessage(), request.getRequestURI(), resultStatus.name()),
                resultStatus
        );
    }

    private void logException(Throwable throwable, HttpServletRequest request) {
        log.debug(
                "В ответ на запрос {}: {} выброшена ошибка: {}",
                request.getMethod(),
                request.getRequestURI(),
                throwable.getMessage());
    }

}
