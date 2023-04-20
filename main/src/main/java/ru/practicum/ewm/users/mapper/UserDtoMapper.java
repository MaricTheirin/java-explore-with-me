package ru.practicum.ewm.users.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.dto.UserResponseDto;
import ru.practicum.ewm.users.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class UserDtoMapper {

    private static final String OBJECT_MAPPED_MESSAGE = "Выполнено преобразование объекта из {} в {}";

    public static User mapDtoToUser (UserDto userDto) {
        User user = new User(
                0L,
                userDto.getName(),
                userDto.getEmail()
        );
        log.trace(OBJECT_MAPPED_MESSAGE, userDto, user);
        return user;
    }

    public static UserResponseDto mapUserToResponseDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
        log.trace(OBJECT_MAPPED_MESSAGE, user, userResponseDto);
        return userResponseDto;
    }

}
