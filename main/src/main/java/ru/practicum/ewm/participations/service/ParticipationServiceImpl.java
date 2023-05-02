package ru.practicum.ewm.participations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.participations.dto.ParticipationResponseDto;
import ru.practicum.ewm.participations.repository.ParticipationRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {

    private final ParticipationRepository participationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationResponseDto> getUserParticipationRequests(long userId) {
        return null;
    }

    @Override
    @Transactional
    public ParticipationResponseDto createParticipationRequest(long userId, long eventId) {
        return null;
    }

    @Override
    @Transactional
    public ParticipationResponseDto cancelParticipationRequest(long userId, long requestId) {
        return null;
    }
}
