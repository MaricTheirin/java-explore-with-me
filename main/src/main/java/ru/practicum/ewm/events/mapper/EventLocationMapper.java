package ru.practicum.ewm.events.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.events.dto.EventLocationDto;
import ru.practicum.ewm.events.dto.EventLocationResponseDto;
import ru.practicum.ewm.events.model.EventLocation;
import ru.practicum.ewm.service.mapper.Mapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class EventLocationMapper extends Mapper {

    public static EventLocation mapDtoToEventLocation(EventLocationDto eventLocationDto) {
        EventLocation mappedLocation = EventLocation.builder()
                .lat(eventLocationDto.getLat())
                .lon(eventLocationDto.getLon())
                .build();
        log.trace(DEFAULT_MESSAGE, eventLocationDto, mappedLocation);
        return mappedLocation;
    }

    public static EventLocationResponseDto mapEventLocationToResponseDto(EventLocation eventLocation) {
        EventLocationResponseDto mappedResponseLocation = new EventLocationResponseDto(
                eventLocation.getLat(),
                eventLocation.getLon()
        );
        log.trace(DEFAULT_MESSAGE, eventLocation, mappedResponseLocation);
        return mappedResponseLocation;
    }

}
