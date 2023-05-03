package ru.practicum.ewm.requests.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.ewm.requests.model.RequestState;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@NoArgsConstructor
@Getter
@ToString
public class RequestStatusUpdateDto {

    @NotEmpty
    Set<Long> requestIds;

    @NotNull
    RequestState status;

}
