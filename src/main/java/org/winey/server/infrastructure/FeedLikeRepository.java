package org.winey.server.infrastructure;

import org.springframework.data.repository.Repository;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.feed.FeedLike;
import org.winey.server.domain.user.User;

import java.util.Optional;

public interface FeedLikeRepository extends Repository<FeedLike,Long> {
    Boolean existsByFeedAndUser(Feed feed, User user);
    int countByFeed(Feed feed);
}
