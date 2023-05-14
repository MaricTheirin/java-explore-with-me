package ru.practicum.ewm.comments.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.comments.model.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByUser_IdOrderByCreatedAsc(long id, Pageable pageable);

    List<Comment> findByEvent_IdOrderByCreatedAsc(long eventId, Pageable pageable);

    Optional<Comment> findByIdAndUser_Id(long id, long userId);

    boolean existsByEvent_IdAndUser_Id(long eventId, long userId);

}
