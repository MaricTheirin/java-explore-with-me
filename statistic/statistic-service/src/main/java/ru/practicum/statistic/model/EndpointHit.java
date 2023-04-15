package ru.practicum.statistic.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.net.InetAddress;
import java.net.URI;
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

    private URI uri;

    private InetAddress ip;

    @CreationTimestamp
    private LocalDateTime timestamp;

}
