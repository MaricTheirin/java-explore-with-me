package ru.practicum.ewm.requests.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.requests.dto.RequestResponseDto;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.service.mapper.Mapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class RequestDtoMapper extends Mapper {

    public static RequestResponseDto mapRequestToResponseDto(Request request) {
        RequestResponseDto responseDto = RequestResponseDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
        log.trace(DEFAULT_MESSAGE, request, responseDto);
        return responseDto;
    }

}
