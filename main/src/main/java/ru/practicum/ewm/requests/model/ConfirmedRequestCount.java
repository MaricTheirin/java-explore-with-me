package ru.practicum.ewm.requests.model;

import lombok.Data;

@Data
public class ConfirmedRequestCount {

    private final Long eventId;
    private final Long confirmed;

}
