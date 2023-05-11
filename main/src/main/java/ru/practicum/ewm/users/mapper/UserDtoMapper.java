package ru.practicum.ewm.users.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.service.mapper.Mapper;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.dto.UserResponseDto;
import ru.practicum.ewm.users.dto.UserShortResponseDto;
import ru.practicum.ewm.users.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class UserDtoMapper extends Mapper {

    public static User mapDtoToUser(UserDto userDto) {
        User user = new User(
                0L,
                userDto.getName(),
                userDto.getEmail()
        );
        log.trace(DEFAULT_MESSAGE, userDto, user);
        return user;
    }

    public static UserResponseDto mapUserToResponseDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
        log.trace(DEFAULT_MESSAGE, user, userResponseDto);
        return userResponseDto;
    }

    public static UserShortResponseDto mapUserToShortResponseDto(User user) {
        UserShortResponseDto userShortResponseDto = new UserShortResponseDto(
                user.getId(),
                user.getName()
        );
        log.trace(DEFAULT_MESSAGE, user, userShortResponseDto);
        return userShortResponseDto;
    }

}
