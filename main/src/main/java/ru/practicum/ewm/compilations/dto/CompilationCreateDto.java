package ru.practicum.ewm.compilations.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.Set;

@Data
public class CompilationCreateDto {

    private Set<Long> events = Collections.emptySet();

    private boolean pinned;

    @NotBlank
    @Length(min = 3, max = 127, message = "Длина должна быть в промежутке от 3 до 127 символов")
    private String title;

}
