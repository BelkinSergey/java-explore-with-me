package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.Marker;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    private List<Long> events;

    private Boolean pinned = false;

    @NotBlank(groups = Marker.OnCreate.class)
    @Size(min = 1, max = 50, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    String title;

}
