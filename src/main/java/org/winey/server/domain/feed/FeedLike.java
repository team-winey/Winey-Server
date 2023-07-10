package org.winey.server.domain.feed;

import lombok.*;
import org.winey.server.domain.user.User;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class FeedLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_like_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="feed_id")
    private Feed feed;

    @Builder
    private FeedLike(User user, Feed feed){
        this.user = user;
        this.feed = feed;
    }
}
