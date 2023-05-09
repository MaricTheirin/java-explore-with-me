package ru.practicum.ewm.requests.model;

import lombok.Data;

@Data
public class ConfirmedRequestCount {

    private Long eventId;
    private Integer confirmed;

}
