package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.events.model.EventStateAction;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

import static ru.practicum.ewm.service.Limit.limitString;

@Data
public class EventUpdateDto {

    @Size(min = 3, max = 500, message = "Длина должна быть в промежутке от 3 до 127 символов")
    private String annotation;

    @Positive
    private Long category;

    @Size(min = 10, max = 2000, message = "Длина должна быть в промежутке от 10 до 127 символов")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @Valid
    private EventLocationDto location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    @Size(min = 3, max = 127, message = "Длина должна быть в промежутке от 3 до 127 символов")
    private String title;

    private EventStateAction stateAction;

    @ToString.Include(name = "annotation")
    private String getLimitedAnnotation() {
        return limitString(annotation, 30);
    }

    @ToString.Include(name = "title")
    private String getLimitedTitle() {
        return limitString(title, 30);
    }

    @ToString.Include(name = "description")
    private String getLimitedDescription() {
        return limitString(description, 30);
    }

}
