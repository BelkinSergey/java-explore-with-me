package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.request.model.Status;

import java.time.LocalDateTime;

import static ru.practicum.constants.DateTime.DATE_TIME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestOutDto {

    private Long id;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime created;

    private Long event;

    private Long requester;

    private Status status;
}
