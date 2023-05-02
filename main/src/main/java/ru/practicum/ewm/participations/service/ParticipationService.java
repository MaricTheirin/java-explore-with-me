package ru.practicum.ewm.participations.service;

import ru.practicum.ewm.participations.dto.ParticipationResponseDto;
import java.util.List;

public interface ParticipationService {

    List<ParticipationResponseDto> getUserParticipationRequests(long userId);

    ParticipationResponseDto createParticipationRequest(long userId, long eventId);

    ParticipationResponseDto cancelParticipationRequest(long userId, long requestId);

}
