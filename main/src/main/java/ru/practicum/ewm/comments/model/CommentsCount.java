package ru.practicum.ewm.comments.model;

import lombok.Data;

@Data
public class CommentsCount {

    private final Long eventId;
    private final Long comments;

}
