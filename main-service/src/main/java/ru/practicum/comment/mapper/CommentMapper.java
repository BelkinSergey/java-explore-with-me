package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getId(),
                comment.getCreated(),
                comment.getUpdatedOn()
        );
    }

    public static Comment toComment(NewCommentDto dto, Event event, User user) {
        return new Comment(
                null,
                dto.getText(),
                user,
                event,
                LocalDateTime.now(),
                null
        );
    }
}
