package ru.practicum.ewm.categories.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class CategoryDto {

    @NotBlank
    private String name;

}
