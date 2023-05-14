package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.categories.dto.CategoryResponseDto;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.users.dto.UserResponseDto;
import java.time.LocalDateTime;

import static ru.practicum.ewm.service.Limit.limitString;

@Value
@Builder
@Jacksonized
public class EventResponseDto {

    long id;
    String title;
    String annotation;
    CategoryResponseDto category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    UserResponseDto initiator;
    EventLocationResponseDto location;
    boolean paid;
    int participantLimit;
    boolean requestModeration;
    EventState state;
    long views;
    long confirmedRequests;
    long comments;

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
