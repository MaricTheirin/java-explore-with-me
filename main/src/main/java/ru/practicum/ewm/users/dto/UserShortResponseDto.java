package ru.practicum.ewm.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class UserShortResponseDto {

    private long id;
    private String name;

}
