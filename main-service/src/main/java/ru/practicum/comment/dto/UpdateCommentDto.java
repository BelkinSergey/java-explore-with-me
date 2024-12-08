package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.event.model.Marker;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentDto {

    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @Size(min = 2, max = 1500, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String text;
}