package ru.practicum.ewm.compilations.dto;

import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CompilationDto {

    @NotNull
    Set<Long> events;

    @NotNull
    Boolean pinned;

    @NotBlank
    String title;

}
