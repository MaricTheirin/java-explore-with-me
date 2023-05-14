package ru.practicum.ewm.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import java.time.LocalDateTime;

@Value
@Builder
public class CommentResponseDto {

    long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updated;

    String text;

    String author;

    Boolean isUpdated;

}
