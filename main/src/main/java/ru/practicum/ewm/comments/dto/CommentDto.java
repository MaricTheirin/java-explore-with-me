package ru.practicum.ewm.comments.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;

@Data
public class CommentDto {

    @NotBlank
    @Length(min = 3, max = 2000)
    String text;

}
