package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.categories.dto.CategoryResponseDto;
import ru.practicum.ewm.users.dto.UserShortResponseDto;
import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class EventShortResponseDto {

    long id;
    String title;
    String annotation;
    CategoryResponseDto category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    UserShortResponseDto initiator;
    boolean paid;
    long views;
    long confirmedRequests;
    long comments;

}
