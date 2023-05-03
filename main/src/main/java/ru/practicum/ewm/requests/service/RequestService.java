package ru.practicum.ewm.requests.service;

import ru.practicum.ewm.requests.dto.RequestResponseDto;
import ru.practicum.ewm.requests.dto.RequestStatusUpdateDto;
import ru.practicum.ewm.requests.dto.RequestStatusUpdateResponseDto;

import java.util.List;

public interface RequestService {

    List<RequestResponseDto> getUserParticipationRequests(long userId);

    RequestResponseDto createRequest(long userId, long eventId);

    RequestResponseDto cancelRequest(long userId, long requestId);

    List<RequestResponseDto> getEventRequests(long userId, long eventId);

    RequestStatusUpdateResponseDto updateEventRequestStatus(
            long userId,
            long eventId,
            RequestStatusUpdateDto statusUpdateDto
    );

}
