package org.winey.server.domain.comment;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.winey.server.domain.AuditingTimeEntity;
import org.winey.server.domain.feed.Feed;
import org.winey.server.domain.user.User;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends AuditingTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @Column(nullable = false)
    private String content;

    @Builder
    public Comment(User user, Feed feed, String content) {
        this.user = user;
        this.feed = feed;
        this.content = content;
    }


}
