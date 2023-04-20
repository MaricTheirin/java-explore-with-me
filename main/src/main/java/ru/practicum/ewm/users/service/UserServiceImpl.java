package ru.practicum.ewm.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.dto.UserResponseDto;
import ru.practicum.ewm.users.exception.UserNotFoundException;
import ru.practicum.ewm.users.mapper.UserDtoMapper;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.users.mapper.UserDtoMapper.mapDtoToUser;
import static ru.practicum.ewm.users.mapper.UserDtoMapper.mapUserToResponseDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDto create(UserDto userDto) {
        User savedUser = userRepository.saveAndFlush(mapDtoToUser(userDto));
        log.debug("Пользователь {} сохранён", savedUser);
        return mapUserToResponseDto(savedUser);
    }

    @Transactional
    @Override
    public void delete(long userId) {
        if (!userRepository.existsById(userId)) {
            log.debug("Пользователь с id = {} не обнаружен", userId);
            throw new UserNotFoundException(userId);
        }
        userRepository.deleteById(userId);
        log.debug("Пользователь с id = {} удалён", userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDto> getAll(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        List<UserResponseDto> requestedUsers = userRepository
                .getAllByIdIn(ids, pageable)
                .stream()
                .map(UserDtoMapper::mapUserToResponseDto)
                .collect(Collectors.toList());
        log.trace("Получен список пользователей: {}", requestedUsers);
        return requestedUsers;
    }

}
