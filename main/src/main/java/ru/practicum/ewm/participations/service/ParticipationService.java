package ru.practicum.ewm.participations.service;

import ru.practicum.ewm.participations.dto.ParticipationResponseDto;
import ru.practicum.ewm.participations.dto.ParticipationsStatusUpdateDto;
import ru.practicum.ewm.participations.dto.ParticipationsStatusUpdateResponseDto;

import java.util.List;

public interface ParticipationService {

    List<ParticipationResponseDto> getUserParticipationRequests(long userId);

    ParticipationResponseDto createParticipationRequest(long userId, long eventId);

    ParticipationResponseDto cancelParticipationRequest(long userId, long requestId);

    List<ParticipationResponseDto> getEventParticipationRequests(long userId, long eventId);

    ParticipationsStatusUpdateResponseDto updateEventParticipationRequestStatus(
            long userId,
            long eventId,
            ParticipationsStatusUpdateDto statusUpdateDto
    );

}
