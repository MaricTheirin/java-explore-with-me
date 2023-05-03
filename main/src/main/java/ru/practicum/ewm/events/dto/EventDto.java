package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.events.model.EventStateAction;
import ru.practicum.ewm.service.validation.Create;
import ru.practicum.ewm.service.validation.Update;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

import static ru.practicum.ewm.service.Limit.limitString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Jacksonized
public class EventDto {

    @NotBlank(groups = Create.class)
    String annotation;

    @Positive(groups = Create.class)
    Long category;

    @NotBlank(groups = Create.class)
    String description;

    @Future(groups = {Create.class, Update.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @NotNull(groups = Create.class)
    EventLocationDto location;

    @NotNull(groups = Create.class)
    Boolean paid;

    @PositiveOrZero
    Integer participantLimit;

    Boolean requestModeration;

    @NotBlank(groups = Create.class)
    String title;

    @Null(groups = Create.class)
    EventStateAction stateAction;

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
