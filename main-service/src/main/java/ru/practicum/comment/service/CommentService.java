package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.model.Comment;

import java.util.List;

public interface CommentService {

    CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto);

    List<CommentDto> getCommentsList(Long userId);

    CommentDto update(Long userId, Long commentId, UpdateCommentDto updateCommentDto);

    void delete(Long userId, Long commentId);

    Comment get(Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);

    List<Comment> getCommentEvent(Long eventId, Integer from, Integer size);
}
