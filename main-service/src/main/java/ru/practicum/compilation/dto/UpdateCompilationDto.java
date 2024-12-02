package ru.practicum.compilation.dto;


import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.Marker;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationDto {

    private List<Long> events;

    @JsonSetter(nulls = Nulls.SKIP)
    private Boolean pinned = false;

    @Size(min = 1, max = 50, groups = {Marker.OnUpdate.class, Marker.OnCreate.class})
    private String title;

}
