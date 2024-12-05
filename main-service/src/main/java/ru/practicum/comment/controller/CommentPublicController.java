package ru.practicum.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentPublicController {

    private final CommentService service;

    @GetMapping("/{event-id}")
    public List<Comment> getAllComments(@PathVariable("event-id") Long eventId,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET запрос на получение всех комментариев своего события с id = {} ", eventId);
        return service.getCommentEvent(eventId, from, size);
    }
}