package ru.practicum.ewm.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.CompilationResponseDto;
import ru.practicum.ewm.compilations.service.CompilationService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto addCompilation(@RequestBody CompilationDto compilationDto) {
        log.info("Запрошено добавление подборки {}", compilationDto);
        return compilationService.create(compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Запрошено удаление подборки с id = {}", compId);
        compilationService.delete(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationResponseDto updateCompilation(
            @PathVariable Long compId,
            @RequestBody CompilationDto compilationDto
    ) {
        log.info("Запрошено обновление подборки с id = {} на {}", compId, compilationDto);
        return compilationService.updateCompilation(compId, compilationDto);
    }

}