package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dao.CommentRepository;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.BadParametersException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository repository;

    @Override
    @Transactional
    public CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new BadParametersException("Невозможно добавить комментарий к событию со статусом не <<PUBLISHED>>");
        }
        return CommentMapper.toCommentDto(repository.save(CommentMapper.toComment(newCommentDto, event, user)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsList(Long userId) {
        checkUser(userId);
        List<Comment> commentList = repository.findByAuthor_Id(userId);
        return commentList.stream().map(CommentMapper::toCommentDto).toList();
    }

    @Override
    public CommentDto update(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {

        User user = checkUser(userId);
        Comment comment = checkComment(commentId);
        checkAuthor(user, comment);

        comment.setText(updateCommentDto.getText());
        comment.setUpdatedOn(LocalDateTime.now());
        return CommentMapper.toCommentDto(repository.save(comment));
    }

    @Override
    public void delete(Long userId, Long commentId) {
        User user = checkUser(userId);
        Comment comment = checkComment(commentId);
        checkAuthor(user, comment);
        repository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment get(Long userId, Long commentId) {
        checkUser(userId);
        return repository.findByAuthor_IdAndId(userId, commentId).orElseThrow(() -> new NotFoundException(
                String.format("У пользователя по id: %d  не найден комментарий по id: %d", userId, commentId)));
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        checkComment(commentId);
        repository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentEvent(Long eventId, Integer from, Integer size) {
        checkEvent(eventId);
        PageRequest pageable = PageRequest.of(from / size, size);
        return repository.findAllByEvent_Id(eventId, pageable);

    }

    private User checkUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь по id: %d  не найден", id)));
    }

    private Event checkEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Событие по id: %d  не найдено", id)));
    }

    private Comment checkComment(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Комментарий по id: %d  не найден", id)));
    }

    private void checkAuthor(User user, Comment comment) {
        if (!comment.getAuthor().equals(user)) {
            throw new BadParametersException("Пользователь не автор комментария");
        }
    }
}
