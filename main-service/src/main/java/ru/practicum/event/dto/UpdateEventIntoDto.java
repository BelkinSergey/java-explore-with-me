package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.Marker;
import ru.practicum.location.dto.LocationDto;

import static ru.practicum.constants.DateTime.DATE_TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventIntoDto {


    @Size(min = 20, max = 2000, groups = Marker.OnUpdate.class)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, groups = Marker.OnUpdate.class)
    private String description;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private String eventDate;

    @Valid
    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero(groups = Marker.OnUpdate.class)
    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    @Size(min = 3, max = 120, groups = Marker.OnUpdate.class)
    private String title;
}
