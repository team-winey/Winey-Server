package org.winey.server.infrastructure;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.user.User;

public interface FeedRepository extends Repository<Feed,Long> {
    void save(Feed feed);
    Optional<Feed> findByFeedId(Long feedId);
    void delete(Feed feed);
    Page<Feed> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Feed> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    Page<Feed> findByUserNotInOrderByCreatedAtDesc(Collection<User> users, Pageable pageable);

    @Query("select sum(f.feedMoney) from Feed f where f.user = :user and f.feedType = 'SAVE' and f.createdAt > :date")
    Long getSavedAmountForPeriod(@Param("user") User user, @Param("date") LocalDateTime date);

    @Query("select sum(f.feedMoney) from Feed f where f.user = :user and f.feedType = 'CONSUME' and f.createdAt > :date")
    Long getSpentAmountForPeriod(@Param("user") User user, @Param("date") LocalDateTime date);
}
