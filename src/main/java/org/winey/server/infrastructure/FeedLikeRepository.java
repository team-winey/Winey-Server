package org.winey.server.infrastructure;

import org.springframework.data.repository.Repository;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.feed.FeedLike;
import org.winey.server.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface FeedLikeRepository extends Repository<FeedLike,Long> {
    // CREATE
    void save(FeedLike feedLike);

    // READ
    Boolean existsByFeedAndUser(Feed feed, User user);
    int countByFeed(Feed feed);

    // UPDATE

    // DELETE
    List<FeedLike> deleteByFeedAndUser(Feed feed, User user);
}
