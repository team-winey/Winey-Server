package org.winey.server.domain.feed;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.winey.server.domain.AuditingTimeEntity;
import org.winey.server.domain.comment.Comment;
import org.winey.server.domain.goal.Goal;
import org.winey.server.domain.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends AuditingTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private Long feedId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Column(nullable = false)
    private String feedTitle;

    @Column(nullable = false)
    private String feedImage;

    @Column(nullable = false)
    private Long feedMoney;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "feed", orphanRemoval = true)
    private List<FeedLike> feedLikes;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "feed", orphanRemoval = true)
    private List<Comment> comments;

    @Builder
    public Feed(User user, String feedTitle, String feedImage, Long feedMoney, Goal goal){
        this.user = user;
        this.feedTitle = feedTitle;
        this.feedImage = feedImage;
        this.feedMoney = feedMoney;
        this.goal = goal;
    }
}
