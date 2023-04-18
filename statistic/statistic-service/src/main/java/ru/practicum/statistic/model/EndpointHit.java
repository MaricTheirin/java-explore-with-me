package ru.practicum.statistic.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "endpoint_hits", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String app;

    private String uri;

    private String ip;

    private LocalDateTime timestamp;

}
