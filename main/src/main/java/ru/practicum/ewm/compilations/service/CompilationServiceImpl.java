package ru.practicum.ewm.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilations.dto.CompilationCreateDto;
import ru.practicum.ewm.compilations.dto.CompilationResponseDto;
import ru.practicum.ewm.compilations.dto.CompilationUpdateDto;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.compilations.repository.CompilationRepository;
import ru.practicum.ewm.events.dto.EventShortResponseDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.service.exception.NotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.ewm.compilations.mapper.CompilationDtoMapper.mapCompilationToResponseDto;
import static ru.practicum.ewm.compilations.mapper.CompilationDtoMapper.mapDtoToCompilation;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;

    @Override
    @Transactional
    public CompilationResponseDto create(CompilationCreateDto compilationDto) {
        Set<Event> connectedEvents = compilationDto.getEvents() != null ?
                eventRepository.findAllByIdIn(compilationDto.getEvents()) :
                Collections.emptySet();
        Compilation savedCompilation =
                compilationRepository.saveAndFlush(mapDtoToCompilation(compilationDto, connectedEvents));
        log.debug("Результат создания подборки: {}", savedCompilation);
        return countRequestsAndMapToResponse(savedCompilation);
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            log.debug("Подборка событий с id = {} не обнаружена", compId);
            throw new NotFoundException(compId);
        }
        compilationRepository.deleteById(compId);
        log.debug("Подборка событий удалена");
    }

    @Override
    @Transactional
    public CompilationResponseDto updateCompilation(Long compId, CompilationUpdateDto compilationDto) {
        Compilation savedCompilation = compilationRepository
                .findById(compId)
                .orElseThrow(() -> new NotFoundException(compId));

        if (compilationDto.getTitle() != null && !compilationDto.getTitle().isBlank()) {
            log.debug(
                    "Изменено наименование подборки с \"{}\" на \"{}\"",
                    savedCompilation.getTitle(),
                    compilationDto.getTitle()
            );
            savedCompilation.setTitle(compilationDto.getTitle());
        }

        if (compilationDto.getPinned() != null) {
            log.debug(
                    "Статус закреплённости подборки изменен с \"{}\" на \"{}\"",
                    savedCompilation.isPinned(),
                    compilationDto.getPinned()
            );
            savedCompilation.setPinned(compilationDto.getPinned());
        }

        if (compilationDto.getEvents() != null) {
            log.debug("Список событий подборки изменён и состоит из следующих id: {}", compilationDto.getEvents());
            savedCompilation.setEvents(eventRepository.findAllByIdIn(compilationDto.getEvents()));
        }

        return countRequestsAndMapToResponse(savedCompilation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationResponseDto> getCompilations(Boolean pinned, int from, int size) {
        List<Compilation> requestedCompilations;
        if (pinned == null) {
            requestedCompilations = compilationRepository.findAll(PageRequest.of(from, size)).toList();
        } else {
            requestedCompilations = compilationRepository.findByPinned(pinned, PageRequest.of(from, size));
        }
        log.debug("Найдены следующие подборки: {}", requestedCompilations);
        return getEventStatsAndMapToResponse(requestedCompilations);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationResponseDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository
                .findById(compId)
                .orElseThrow(() -> new NotFoundException(compId));
        log.debug("Получена подборка {}", compilation);
        return countRequestsAndMapToResponse(compilation);
    }

    private CompilationResponseDto countRequestsAndMapToResponse(Compilation compilation) {
        return getEventStatsAndMapToResponse(List.of(compilation)).stream().findFirst().orElseThrow();
    }

    private List<CompilationResponseDto> getEventStatsAndMapToResponse(Collection<Compilation> compilations) {
        Map<Long, EventShortResponseDto> connectedEventsWithStats = eventService
                .getStatsAndMapToShortResponseDtos(compilations
                        .stream()
                        .map(Compilation::getEvents)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(EventShortResponseDto::getId, Function.identity()));

        return compilations
                .stream()
                .map(compilation -> mapCompilationToResponseDto(compilation, connectedEventsWithStats))
                .collect(Collectors.toList());
    }

}