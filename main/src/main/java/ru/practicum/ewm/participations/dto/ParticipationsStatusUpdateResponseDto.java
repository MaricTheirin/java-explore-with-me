package ru.practicum.ewm.participations.dto;

import lombok.Value;
import java.util.HashSet;
import java.util.Set;

@Value
public class ParticipationsStatusUpdateResponseDto {

    Set<ParticipationResponseDto> confirmedRequests = new HashSet<>();

    Set<ParticipationResponseDto> rejectedRequests = new HashSet<>();

}
