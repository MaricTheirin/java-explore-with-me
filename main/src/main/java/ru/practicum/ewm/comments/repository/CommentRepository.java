package ru.practicum.ewm.comments.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.comments.model.CommentsCount;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByUser_IdOrderByCreatedAsc(long id, Pageable pageable);

    List<Comment> findByEvent_IdOrderByCreatedAsc(long eventId, Pageable pageable);

    Optional<Comment> findByIdAndUser_Id(long id, long userId);

    boolean existsByEvent_IdAndUser_Id(long eventId, long userId);

    @Query("SELECT new ru.practicum.ewm.comments.model.CommentsCount(c.event.id, COUNT(c.id)) " +
            "FROM Comment AS c " +
            "WHERE c.event.id IN :eventIds " +
            "GROUP BY c.event.id " +
            "ORDER BY c.event.id ASC"
    )
    List<CommentsCount> countAllByEvent_Id(Collection<Long> eventIds);

    long countByEvent_Id(long eventId);

}
