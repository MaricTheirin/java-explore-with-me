package ru.practicum.ewm.participations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.participations.model.Participation;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

}
