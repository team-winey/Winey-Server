package org.winey.server.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.winey.server.domain.comment.Comment;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.feed.FeedLike;
import org.winey.server.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends Repository<Comment,Long> {
    Long countByFeed(Feed feed);

    Optional<Comment> findByCommentId(Long feedId);

    void save(Comment comment);

    Long deleteByCommentId(Long commentId);

    List<Comment> findAllByFeedOrderByCreatedAtDesc(Feed feed);


}
