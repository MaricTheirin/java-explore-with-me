package ru.practicum.ewm.requests.dto;

import lombok.Data;
import ru.practicum.ewm.requests.model.RequestState;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class RequestStatusUpdateDto {

    @NotEmpty
    private Set<Long> requestIds;

    @NotNull
    private RequestState status;

}
