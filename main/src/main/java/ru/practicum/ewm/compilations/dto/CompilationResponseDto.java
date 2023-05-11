package ru.practicum.ewm.compilations.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.ewm.events.dto.EventShortResponseDto;
import java.util.List;

@Value
@Builder
public class CompilationResponseDto {
    List<EventShortResponseDto> events;
    long id;
    boolean pinned;
    String title;
}
