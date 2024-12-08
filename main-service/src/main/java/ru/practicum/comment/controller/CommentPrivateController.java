package ru.practicum.comment.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.service.CommentService;
import ru.practicum.event.model.Marker;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentPrivateController {

    CommentService service;

    @PostMapping("/users/{user-id}/events/{event-id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable("user-id") Long userId,
                             @PathVariable("event-id") Long eventId,
                             @Validated(Marker.OnCreate.class) @RequestBody NewCommentDto newCommentDto) {
        log.info("POST запрос на добавление комментария: {}", newCommentDto);
        return service.create(userId, eventId, newCommentDto);
    }

    @PatchMapping("/users/{user-id}/{comment-id}")
    public CommentDto update(@PathVariable("user-id") Long userId,
                             @PathVariable("comment-id") Long commentId,
                             @Validated(Marker.OnUpdate.class) @RequestBody UpdateCommentDto updateCommentDto) {

        log.info("Запрос на обновление пользователем по id: {}  комментария по id: {} ", userId, commentId);
        return service.update(userId, commentId, updateCommentDto);
    }

    @GetMapping("/users/{user-id}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentListByUser(@PathVariable("user-id") Long userId) {
        log.info("Запрос на получение комментариев пользователя id: {} ", userId);
        return service.getCommentsList(userId);
    }

    @DeleteMapping("/users/{user-id}/{comment-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("user-id") Long userId, @PathVariable("comment-id") Long commentId) {
        log.info("Запрос на удаление комментария id: {} пользователем id: {} ", userId, commentId);
        service.delete(userId, commentId);
    }

    @GetMapping("/users/{user-id}/{comment-id}")
    @ResponseStatus(HttpStatus.OK)
    public Comment get(@PathVariable("user-id") Long userId, @PathVariable("comment-id") Long commentId) {
        log.info("GET запрос на получения комментария id: {} пользователем id: {} ", commentId, userId);
        return service.get(userId, commentId);
    }


}
