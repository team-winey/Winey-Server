package org.winey.server.infrastructure;

import org.springframework.data.repository.Repository;
import org.winey.server.domain.comment.Comment;
import org.winey.server.domain.feed.Feed;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends Repository<Comment,Long> {
    // CREATE
    void save(Comment comment);

    // READ
    Long countByFeed(Feed feed);

    Optional<Comment> findByCommentId(Long feedId);

    List<Comment> findAllByFeedOrderByCreatedAtAsc(Feed feed);

    // DELETE
    Long deleteByCommentId(Long commentId);


}
