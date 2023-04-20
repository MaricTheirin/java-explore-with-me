package ru.practicum.ewm.users.dto;

import lombok.Data;
import ru.practicum.ewm.service.validation.Create;
import ru.practicum.ewm.service.validation.Update;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {

    @NotBlank
    private final String name;

    @NotBlank
    @Email(
            regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
            groups = {Create.class, Update.class},
            message = "Email задан некорректно"
    )
    private final String email;

}
