package ru.practicum.ewm.requests.dto;

import lombok.Value;
import java.util.HashSet;
import java.util.Set;

@Value
public class RequestStatusUpdateResponseDto {

    Set<RequestResponseDto> confirmedRequests = new HashSet<>();

    Set<RequestResponseDto> rejectedRequests = new HashSet<>();

}
