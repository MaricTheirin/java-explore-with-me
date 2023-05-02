package ru.practicum.ewm.participations.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.users.model.User;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participations", schema = "public")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @CreationTimestamp
    private LocalDateTime created;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User requester;

    private ParticipationState status;

}
