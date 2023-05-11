package ru.practicum.ewm.users.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {

    @NotBlank
    @Length(min = 3, max = 127, message = "Длина должна быть в промежутке от 3 до 127 символов")
    private final String name;

    @NotBlank
    @Email(
            regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
            message = "Email задан некорректно"
    )
    @Length(min = 3, max = 127, message = "Длина должна быть в промежутке от 3 до 127 символов")
    private final String email;

}
