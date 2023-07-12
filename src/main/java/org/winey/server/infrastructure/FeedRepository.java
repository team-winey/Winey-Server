package org.winey.server.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface FeedRepository extends Repository<Feed,Long> {
    void save(Feed feed);
    Optional<Feed> findByFeedId(Long feedId);
    void delete(Feed feed);
    Page<Feed> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Feed> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
