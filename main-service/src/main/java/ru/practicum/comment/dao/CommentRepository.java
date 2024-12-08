package ru.practicum.comment.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByAuthor_Id(Long userId);

    Optional<Comment> findByAuthor_IdAndId(Long userId, Long commentId);

    List<Comment> findAllByEvent_Id(Long eventId, PageRequest pageable);
}
