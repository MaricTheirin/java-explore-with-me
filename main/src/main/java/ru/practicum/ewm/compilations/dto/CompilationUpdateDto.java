package ru.practicum.ewm.compilations.dto;

import lombok.Data;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class CompilationUpdateDto {

    private Set<Long> events;

    private Boolean pinned;

    @Size(min = 3, max = 127, message = "Длина должна быть в промежутке от 3 до 127 символов")
    private String title;

}
