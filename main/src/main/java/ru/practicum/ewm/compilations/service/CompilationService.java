package ru.practicum.ewm.compilations.service;

import ru.practicum.ewm.compilations.dto.CompilationCreateDto;
import ru.practicum.ewm.compilations.dto.CompilationResponseDto;
import ru.practicum.ewm.compilations.dto.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {

    CompilationResponseDto create(CompilationCreateDto compilationDto);

    void delete(Long compId);

    CompilationResponseDto updateCompilation(Long compId, CompilationUpdateDto compilationDto);

    List<CompilationResponseDto> getCompilations(Boolean pinned, int from, int size);

    CompilationResponseDto getCompilation(Long compId);

}
