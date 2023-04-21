package ru.practicum.ewm.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.events.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

    boolean existsByCategoryId(long categoryId);

}
