package ru.practicum.ewm.participations.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.participations.model.ParticipationState;
import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class ParticipationResponseDto {

    long id;

    long event;

    long requester;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created;

    ParticipationState status;

}
