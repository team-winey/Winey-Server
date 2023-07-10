package org.winey.server.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import org.winey.server.domain.feed.Feed;

import java.util.Optional;

public interface FeedRepository extends Repository<Feed,Long> {
    void save(Feed feed);
    Optional<Feed> findByFeedId(Long feedId);
    void delete(Feed feed);
}
