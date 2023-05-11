package ru.practicum.ewm.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.events.model.EventLocation;

public interface EventLocationRepository extends JpaRepository<EventLocation, Long> {
}
