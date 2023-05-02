package ru.practicum.ewm.compilations.dto;

import lombok.*;
import ru.practicum.ewm.service.validation.Create;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CompilationDto {

    @NotEmpty(groups = Create.class)
    Set<Long> events;

    @NotNull(groups = Create.class)
    Boolean pinned;

    @NotNull
    @NotBlank
    String title;

}
