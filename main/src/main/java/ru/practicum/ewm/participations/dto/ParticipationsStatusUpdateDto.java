package ru.practicum.ewm.participations.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.ewm.participations.model.ParticipationState;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@NoArgsConstructor
@Getter
@ToString
public class ParticipationsStatusUpdateDto {

    @NotEmpty
    Set<Long> requestIds;

    @NotNull
    ParticipationState status;

}
