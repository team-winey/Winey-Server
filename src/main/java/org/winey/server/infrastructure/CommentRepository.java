package org.winey.server.infrastructure;

import org.springframework.data.repository.Repository;
import org.winey.server.domain.comment.Comment;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.feed.FeedLike;

public interface CommentRepository extends Repository<Comment,Long> {
    int countByFeed(Feed feed);

    void save(Comment comment);
}
