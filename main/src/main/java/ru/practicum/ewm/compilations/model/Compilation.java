package ru.practicum.ewm.compilations.model;

import lombok.*;
import ru.practicum.ewm.events.model.Event;
import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@NamedEntityGraph(
        name = "compilation-event-graph",
        attributeNodes = @NamedAttributeNode(value = "events")
)
@Entity
@Table(name = "compilations", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> events = new LinkedHashSet<>();

    @Column
    private boolean pinned;

    @Column(unique = true)
    private String title;

}
