package ru.practicum.ewm.users.service;

import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto create(UserDto userDto);

    void delete(long userId);

    List<UserResponseDto> getAll(List<Long> ids, int from, int size);

}
