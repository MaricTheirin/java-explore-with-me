package ru.practicum.ewm.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserResponseDto {

    private long id;
    private String name;
    private String email;

}
