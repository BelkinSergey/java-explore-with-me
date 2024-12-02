package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryOutDto;
import ru.practicum.event.model.State;
import ru.practicum.location.model.Location;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.constants.DateTime.DATE_TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EventOutFullDto {

    private String annotation;

    private CategoryOutDto category;

    private Integer confirmedRequests;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    private Long id;

    private UserShortDto initiator;


    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private State state;

    private String title;

    private Long views;

}
