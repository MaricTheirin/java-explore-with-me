package ru.practicum.ewm.participations.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.participations.dto.ParticipationResponseDto;
import ru.practicum.ewm.participations.model.Participation;
import ru.practicum.ewm.service.mapper.Mapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ParticipationDtoMapper extends Mapper {

    public static ParticipationResponseDto mapParticipationToResponseDto(Participation participation) {
        ParticipationResponseDto responseDto = ParticipationResponseDto.builder()
                .id(participation.getId())
                .created(participation.getCreated())
                .event(participation.getEvent().getId())
                .requester(participation.getRequester().getId())
                .status(participation.getStatus())
                .build();
        log.trace(DEFAULT_MESSAGE, participation, responseDto);
        return responseDto;
    }

}
