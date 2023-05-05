package ru.practicum.ewm.users.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.service.validation.Create;
import ru.practicum.ewm.service.validation.Update;
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
            groups = {Create.class, Update.class},
            message = "Email задан некорректно"
    )
    @Length(min = 3, max = 127, message = "Длина должна быть в промежутке от 3 до 127 символов")
    private final String email;

}
