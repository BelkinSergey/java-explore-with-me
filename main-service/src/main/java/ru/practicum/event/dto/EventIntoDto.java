package ru.practicum.event.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.Marker;
import ru.practicum.location.dto.LocationDto;

import java.time.LocalDateTime;

import static ru.practicum.constants.DateTime.DATE_TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EventIntoDto {
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(min = 20, max = 2000, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String annotation;

    @NotNull(groups = Marker.OnCreate.class)
    private Long category;

    @Size(min = 20, max = 7000, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @NotBlank(groups = Marker.OnCreate.class)
    private String description;

    @NotNull(groups = Marker.OnCreate.class)
    @Future(groups = Marker.OnCreate.class)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    @NotNull(groups = Marker.OnCreate.class)
    private LocationDto location;
    @JsonSetter(nulls = Nulls.SKIP)
    private Boolean paid = false;

    @JsonSetter(nulls = Nulls.SKIP)
    @PositiveOrZero(groups = Marker.OnCreate.class)
    private Integer participantLimit = 0;

    @JsonSetter(nulls = Nulls.SKIP)
    private Boolean requestModeration = true;

    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @Size(min = 3, max = 120, groups = Marker.OnCreate.class)
    private String title;

}
