package ru.practicum.ewm.compilations.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.compilations.dto.CompilationCreateDto;
import ru.practicum.ewm.compilations.dto.CompilationResponseDto;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.events.dto.EventShortResponseDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.service.mapper.Mapper;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.ewm.events.mapper.EventDtoMapper.mapEventToShortResponseDto;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationDtoMapper extends Mapper {

    public static Compilation mapDtoToCompilation(CompilationCreateDto compilationDto, Set<Event> events) {
        Compilation compilation = Compilation.builder()
                .title(compilationDto.getTitle())
                .events(events)
                .pinned(compilationDto.isPinned())
                .build();
        log.trace(DEFAULT_MESSAGE, compilationDto, compilation);
        return compilation;
    }

    public static CompilationResponseDto mapCompilationToResponseDto(
            Compilation compilation,
            Map<Long, Integer> confirmedRequests
    ) {

        List<EventShortResponseDto> connectedEvents = compilation
                .getEvents()
                .stream()
                .map(event -> mapEventToShortResponseDto(event, confirmedRequests.getOrDefault(event.getId(), 0)))
                .collect(Collectors.toList());

        CompilationResponseDto responseDto = CompilationResponseDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(connectedEvents)
                .build();
        log.trace(DEFAULT_MESSAGE, compilation, responseDto);
        return responseDto;
    }

}
