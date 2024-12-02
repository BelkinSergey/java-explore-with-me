package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ConfirmedRequests {

    private Long count;

    private Long event;
}
